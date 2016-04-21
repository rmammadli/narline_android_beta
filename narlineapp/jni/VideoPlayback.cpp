/*==============================================================================
Copyright (c) 2012-2013 QUALCOMM Austria Research Center GmbH.
All Rights Reserved.

This  Vuforia(TM) sample application in source code form ("Sample Code") for the
Vuforia Software Development Kit and/or Vuforia Extension for Unity
(collectively, the "Vuforia SDK") may in all cases only be used in conjunction
with use of the Vuforia SDK, and is subject in all respects to all of the terms
and conditions of the Vuforia SDK License Agreement, which may be found at
https://developer.vuforia.com/legal/license.

By retaining or using the Sample Code in any manner, you confirm your agreement
to all the terms and conditions of the Vuforia SDK License Agreement.  If you do
not agree to all the terms and conditions of the Vuforia SDK License Agreement,
then you may not retain or use any of the Sample Code in any manner.


@file
    VideoPlayback.cpp

@brief
    Sample for VideoPlayback

==============================================================================*/


//Include headers for Cloud Reco
#include <QCAR/ImageTargetResult.h>
#include <QCAR/ImageTarget.h>
#include <QCAR/TargetFinder.h>
#include <QCAR/TargetSearchResult.h>
#include <QCAR/TrackableSource.h>
#include <QCAR/Image.h>

#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <math.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <QCAR/QCAR.h>
#include <QCAR/CameraDevice.h>
#include <QCAR/Renderer.h>
#include <QCAR/VideoBackgroundConfig.h>
#include <QCAR/Trackable.h>
#include <QCAR/TrackableResult.h>
#include <QCAR/Tool.h>
#include <QCAR/Tracker.h>
#include <QCAR/TrackerManager.h>
#include <QCAR/ObjectTracker.h>
#include <QCAR/CameraCalibration.h>
#include <QCAR/DataSet.h>
#include <QCAR/ImageTarget.h>
#include <QCAR/Vectors.h>
#include <QCAR/UpdateCallback.h>

#include "SampleUtils.h"
#include "SampleMath.h"
#include "Texture.h"
#include "VideoPlaybackShaders.h"
#include "KeyframeShaders.h"
#include "Quad.h"

#ifdef __cplusplus
extern "C"
{
#endif


    /////////////////////////////////////////
    //New global vars for Cloud Reco
    bool scanningMode = false;
    bool showStartScanButton = false;
    static const size_t CONTENT_MAX = 256;
    char lastTargetId[CONTENT_MAX];
    char targetMetadata[CONTENT_MAX];
    /////////////////////////////////////////
    static const char* kAccessKey = "1cc5e01ff1e7f2de73ff35edb8e07523663353c5";
    static const char* kSecretKey = "3040c3daec1b8b7edf2a90bbffe65e01347b36bc";

    // Initialize State Variables for Cloud Reco
    void initStateVariables()
    {
        lastTargetId[0] = '\0';
        scanningMode = false;
    }



// Textures:
int textureCount                        =  0;
Texture** textures                      =  0;

// Please mind that these values are the same for both Java and native
// See: src/com/qualcomm/QCARSamples/VideoPlayback/VideoPlayerHelper.java
enum MEDIA_STATE {
    REACHED_END                         =  0,
    PAUSED                              =  1,
    STOPPED                             =  2,
    PLAYING                             =  3,
    READY                               =  4,
    NOT_READY                           =  5,
    ERROR                               =  6
};


// ----------------------------------------------------------------------------
// Methods Ids for calling Java Functions from Native Code
// ----------------------------------------------------------------------------
jmethodID createProductTextureID = 0;







// ----------------------------------------------------------------------------
// JNI Handles to the JavaVM and Activity instance:
// ----------------------------------------------------------------------------
JavaVM* javaVM = 0;
jobject activityObj = 0;

static const int NUM_TEXTURES = 9;
static const int NUM_TARGETS  = 2;
static const int STONES = 0;
static const int CHIPS = 1;

MEDIA_STATE currentStatus[NUM_TARGETS];

// Video Playback Rendering Specific
unsigned int videoPlaybackShaderID      =  0;
GLint videoPlaybackVertexHandle         =  0;
GLint videoPlaybackNormalHandle         =  0;
GLint videoPlaybackTexCoordHandle       =  0;
GLint videoPlaybackMVPMatrixHandle      =  0;
GLint videoPlaybackTexSamplerOESHandle  =  0;

// Video Playback Textures for the two targets
GLuint videoPlaybackTextureID[NUM_TARGETS];

// Keyframe and icon rendering specific
unsigned int keyframeShaderID           =  0;
GLint keyframeVertexHandle              =  0;
GLint keyframeNormalHandle              =  0;
GLint keyframeTexCoordHandle            =  0;
GLint keyframeMVPMatrixHandle           =  0;
GLint keyframeTexSampler2DHandle        =  0;


// Button Texture Coordinates from target center
int buttonCoords[NUM_TEXTURES][2] = {
	{0,0},
	{0,0},
	{0,0},
	{0,0},
	{0,0},
	{-200,-200},
	{ 0  ,-200},
	{ 200,-200},
};


// We cannot use the default texture coordinates of the quad since these will change depending on the video itself
GLfloat videoQuadTextureCoords[] = {
    0.0f, 0.0f,
    1.0f, 0.0f,
    1.0f, 1.0f,
    0.0f, 1.0f,
};

// This variable will hold the transformed coordinates (changes every frame)
GLfloat videoQuadTextureCoordsTransformedStones[] = {
    0.0f, 0.0f,
    1.0f, 0.0f,
    1.0f, 1.0f,
    0.0f, 1.0f,
};

GLfloat videoQuadTextureCoordsTransformedChips[] = {
    0.0f, 0.0f,
    1.0f, 0.0f,
    1.0f, 1.0f,
    0.0f, 1.0f,
};

// Screen dimensions:
unsigned int screenWidth                = 0;
unsigned int screenHeight               = 0;

// Trackable dimensions
QCAR::Vec3F targetPositiveDimensions[NUM_TARGETS];

// Indicates whether screen is in portrait (true) or landscape (false) mode
bool isActivityInPortraitMode           = false;

bool isTracking[NUM_TARGETS];

// The projection matrix used for rendering virtual objects:
QCAR::Matrix44F projectionMatrix;

// These hold the aspect ratio of both the video and the
// keyframe
float videoQuadAspectRatio[NUM_TARGETS];
float keyframeQuadAspectRatio[NUM_TARGETS];

QCAR::DataSet* dataSetStonesAndChips    = 0;

QCAR::Matrix44F inverseProjMatrix;

// Needed to calculate whether a screen tap is inside the target
QCAR::Matrix44F modelViewMatrix[NUM_TARGETS];

// Needed to calculate whether a screen tap is inside the target
QCAR::Matrix44F modelViewMatrixForTextures[NUM_TEXTURES];


JNIEXPORT int JNICALL
Java_com_newo_newoapp_activities_ScanActivity_getOpenGlEsVersionNative(JNIEnv *, jobject)
{
    // We only use OpenGL ES 2.0
    return 2;
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_setActivityPortraitMode(JNIEnv *, jobject, jboolean isPortrait)
{
    isActivityInPortraitMode = isPortrait;
}


    JNIEXPORT int JNICALL
    Java_com_newo_newoapp_activities_ScanActivity_initCloudReco(
                                                                          JNIEnv *, jobject)
    {
        LOG("Java_com_qualcomm_QCARSamples_ImageTargets_ImageTargets_initCloudReco");

        QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
        QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                                                                            trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));

        assert(objectTracker != NULL);


        //Get the TargetFinder:
        QCAR::TargetFinder* targetFinder = objectTracker->getTargetFinder();
        assert(targetFinder != NULL);

        // Start initialization:
        if (targetFinder->startInit(kAccessKey, kSecretKey))
        {
            targetFinder->waitUntilInitFinished();
        }

        int resultCode = targetFinder->getInitState();
        if ( resultCode != QCAR::TargetFinder::INIT_SUCCESS)
        {
            LOG("Failed to initialize target finder.");
            return resultCode;
        }

        // Use the following calls if you would like to customize the color of the UI
        //targetFinder->setUIScanlineColor(0.2, 0.7, 0.89);
        targetFinder->setUIScanlineColor(0.64, 0.13, 0.55);
        //targetFinder->setUIPointColor(0.2, 0.7, 0.9);
		targetFinder->setUIPointColor(0.64, 0.13, 0.55);

        return resultCode;
    }

    JNIEXPORT int JNICALL
    Java_com_newo_newoapp_activities_ScanActivity_deinitCloudReco(
                                                                            JNIEnv *, jobject)
    {

    	LOG("Java_com_newo_newoapp_activities_ScanActivity_deinitCloudReco");


        // Get the image tracker:
        QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
        QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                                                                            trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));

        if (objectTracker == NULL)
        {
            LOG("Failed to deinit CloudReco as the ObjectTracker was not initialized.");
            return 0;
        }

        // Deinitialize Cloud Reco:
        QCAR::TargetFinder* finder = objectTracker->getTargetFinder();
        finder->deinit();

        return 1;
    }



    JNIEXPORT void JNICALL
    Java_com_newo_newoapp_activities_ScanActivity_enterScanningModeNative(
                                                                                    JNIEnv* env, jobject obj)
    {
    	LOG("Java_com_newo_newoapp_activities_ScanActivity_enterScanningModeNative");

        QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
        QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                                                                            trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));
        assert(objectTracker != 0);

        QCAR::TargetFinder* targetFinder = objectTracker->getTargetFinder();
        assert (targetFinder != 0);

        // Start Cloud Reco
        targetFinder->startRecognition();

        // Clear all trackables created previously:
        targetFinder->clearTrackables();

        scanningMode = true;
    }



    JNIEXPORT void JNICALL
        Java_com_newo_newoapp_activities_ScanActivity_stopScanningModeNative(
                                                                                        JNIEnv* env, jobject obj)
        {
        	LOG("Java_com_newo_newoapp_activities_ScanActivity_stopScanningModeNative");

            QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
            QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                                                                                trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));

            assert(objectTracker != 0);

            QCAR::TargetFinder* targetFinder = objectTracker->getTargetFinder();
            assert (targetFinder != 0);

            // Start Cloud Reco
            targetFinder->stop();

            // Clear all trackables created previously:
            targetFinder->clearTrackables();

            scanningMode = false;
        }


    // ----------------------------------------------------------------------------
    /** Calls the Java Method to start loading the product texture*/
    // ----------------------------------------------------------------------------
    void
    createProductTexture(const char* targetMetadata)
    {


    	LOG("createProductTexture");

        // Check that the JNI handles are setup correctly:
        JNIEnv* env = 0;
        /*
        if (javaVM == 0 )
        {
        	LOG("createProductTextureCall_javaVM=0");
        }

        if (createProductTextureID == 0 )
        {
            LOG("createProductTextureCall_createProductTextureID=0");
        }

        if (activityObj == 0 )
        {
            LOG("createProductTextureCall_activityObj=0");
        }

        if (javaVM->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK)
        {
            LOG("createProductTextureCall_JNI_VERSION_1_4");
        }*/

        if (javaVM != 0 && createProductTextureID != 0 && activityObj != 0
                && javaVM->GetEnv((void**)&env, JNI_VERSION_1_4) == JNI_OK)
        {
        	LOG("createProductTextureCallJava");

            env->CallVoidMethod(activityObj, createProductTextureID, env->NewStringUTF(targetMetadata));

        }




 }



    // Object to receive update callbacks from QCAR SDK
    class ImageTargets_UpdateCallback : public QCAR::UpdateCallback
    {
        virtual void QCAR_onUpdate(QCAR::State& state)
        {

        	LOG("imageTargets_UpdateCallback UpdateCallback");

            //NEW code for Cloud Reco
            QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
            QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                                                                               trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));

            // Get the target finder:
            QCAR::TargetFinder* targetFinder = objectTracker->getTargetFinder();

            // Check if there are new results available:
            const int statusCode = targetFinder->updateSearchResults();

            if (statusCode < 0)
            {
                char errorMessage[80];

                sprintf(errorMessage, "Error with status code %d at frame %d", statusCode,
                        state.getFrame().getTimeStamp());
            }
            else if (statusCode == QCAR::TargetFinder::UPDATE_RESULTS_AVAILABLE)
            {
                // Process new search results
                if (targetFinder->getResultCount() > 0)
                {
                    const QCAR::TargetSearchResult* result = targetFinder->getResult(0);

                    // Check if this target is suitable for tracking:
                    if (result->getTrackingRating() > 0)
                    {
                        // Create a new Trackable from the result:
                        QCAR::Trackable* newTrackable = targetFinder->enableTracking(*result);


                        if (newTrackable != 0)
                        {
                            LOG("Successfully created new trackable '%s' with rating '%d' with targetId '%s'.",

                                newTrackable->getName(), result->getTrackingRating(),result->getUniqueTargetId());

                            // Checks if the targets has changed
                            LOG( "Comparing Strings. currentTargetId: %s  lastTargetId: %s",
                                           result->getUniqueTargetId(), lastTargetId);


                            if (strcmp(result->getUniqueTargetId(), lastTargetId) != 0)
                            {
                                // If the target has changed...
                                // app-specific: do something
                                // (e.g. generate new 3D model or texture)

                            	// Copies the new target Metadata
                            	//snprintf(targetMetadata, CONTENT_MAX, "%s", result->getMetaData());
                            	snprintf(targetMetadata, CONTENT_MAX, "%s", result->getUniqueTargetId());

                            	// Calls the Java method with the current product texture
                            	createProductTexture(targetMetadata);

                            }

                            strcpy(lastTargetId, result->getUniqueTargetId());

                            // Stop Cloud Reco scanning
                            targetFinder->stop();
                            scanningMode = false;
                            showStartScanButton = true;


                        }
                        else
                        	LOG("Failed to create new trackable.");
                    }

                }
            }



        }
    };

    ImageTargets_UpdateCallback updateCallback;


    // ----------------------------------------------------------------------------
    // Activates Camera Flash
    // ----------------------------------------------------------------------------
    JNIEXPORT jboolean JNICALL
    Java_com_newo_newoapp_activities_ScanActivity_activateFlash(JNIEnv*, jobject, jboolean flash)
    {
        return QCAR::CameraDevice::getInstance().setFlashTorchMode((flash==JNI_TRUE)) ? JNI_TRUE : JNI_FALSE;
    }

JNIEXPORT int JNICALL
Java_com_newo_newoapp_activities_ScanActivity_initTracker(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_initTracker");

    // Initialize the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* tracker = trackerManager.initTracker(QCAR::ObjectTracker::getClassType());
    if (tracker == NULL)
    {
        LOG("Failed to initialize ObjectTracker.");
        return 0;
    }

    for (int i=0; i<NUM_TARGETS; i++)
    {
        targetPositiveDimensions[i].data[0] = 0.0;
        targetPositiveDimensions[i].data[1] = 0.0;
        videoPlaybackTextureID[i] = -1;
    }

    LOG("Successfully initialized ObjectTracker.");
    return 1;
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_deinitTracker(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_deinitTracker");

    // Deinit the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    trackerManager.deinitTracker(QCAR::ObjectTracker::getClassType());
}


JNIEXPORT int JNICALL
Java_com_newo_newoapp_activities_ScanActivity_loadTrackerData(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_loadTrackerData");

    // Get the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                    trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));
    if (objectTracker == NULL)
    {
        LOG("Failed to load tracking data set because the ObjectTracker has not"
            " been initialized.");
        return 0;
    }

    // Create the data sets:
    dataSetStonesAndChips = objectTracker->createDataSet();
    if (dataSetStonesAndChips == 0)
    {
        LOG("Failed to create a new tracking data.");
        return 0;
    }

    // Load the data sets:
    if (!dataSetStonesAndChips->load("StonesAndChips.xml", QCAR::DataSet::STORAGE_APPRESOURCE))
    {
        LOG("Failed to load data set.");
        return 0;
    }

    // Activate the data set:
    if (!objectTracker->activateDataSet(dataSetStonesAndChips))
    {
        LOG("Failed to activate data set.");
        return 0;
    }

    LOG("Successfully loaded and activated data set.");
    return 1;
}


JNIEXPORT int JNICALL
Java_com_newo_newoapp_activities_ScanActivity_destroyTrackerData(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_destroyTrackerData");

    // Get the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
        trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));
    if (objectTracker == NULL)
    {
        LOG("Failed to destroy the tracking data set because the ObjectTracker has not"
            " been initialized.");
        return 0;
    }

    if (dataSetStonesAndChips != 0)
    {
        if (objectTracker->getActiveDataSet() == dataSetStonesAndChips &&
            !objectTracker->deactivateDataSet(dataSetStonesAndChips))
        {
            LOG("Failed to destroy the tracking data set StonesAndChips because the data set "
                "could not be deactivated.");
            return 0;
        }

        if (!objectTracker->destroyDataSet(dataSetStonesAndChips))
        {
            LOG("Failed to destroy the tracking data set StonesAndChips.");
            return 0;
        }

        LOG("Successfully destroyed the data set StonesAndChips.");
        dataSetStonesAndChips = 0;
    }

    return 1;
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_onQCARInitializedNative(JNIEnv *, jobject)
{
	 // Register the update callback where we handle the data set swap:
	 QCAR::registerCallback(&updateCallback);

     QCAR::setHint(QCAR::HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 2);
}

JNIEXPORT jint JNICALL
Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_getVideoTextureID(JNIEnv* env, jobject obj, jint target )
{
	LOG("Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_getVideoTextureID");
    // Return the texture for the video playback created in native
    return videoPlaybackTextureID[target];
}

JNIEXPORT void JNICALL
Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_setStatus(JNIEnv* env, jobject obj, jint target, jint value)
{

	LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlaybackRenderer_setStatus");

    // Transform the value passed from java to our own values
    switch (value)
    {
        case 0: currentStatus[target] = REACHED_END;   break;
        case 1: currentStatus[target] = PAUSED;        break;
        case 2: currentStatus[target] = STOPPED;       break;
        case 3: currentStatus[target] = PLAYING;       break;
        case 4: currentStatus[target] = READY;         break;
        case 5: currentStatus[target] = NOT_READY;     break;
        case 6: currentStatus[target] = ERROR;         break;
        default:currentStatus[target] = NOT_READY;     break;
    }
}



//check is tapped to appropriate texture
bool isTapOnScreenInsideTexture(int textureNumber, float x, float y) {

	LOG("isTapOnScreenInsideTexture");
	// Here we calculate that the touch event is inside the target
	QCAR::Vec3F intersection, lineStart, lineEnd;

	SampleMath::projectScreenPointToPlane(inverseProjMatrix,
			modelViewMatrix[1], screenWidth, screenHeight,
			QCAR::Vec2F(x, y), QCAR::Vec3F(0, 0, 0), QCAR::Vec3F(0, 0, 1),
			intersection, lineStart, lineEnd);


	LOG("isTapCoordinates textureNumber: %d  X :%f Y :%f " , textureNumber,intersection.data[0],intersection.data[1]);

	LOG("isTapTargetPositiveRealDimensions X :%d Y :%d " ,
			buttonCoords[textureNumber][0],
			buttonCoords[textureNumber][1]);

	/*
	LOG("isTapTargetPositiveDimensions X1 :%f X2 :%f Y1 :%f Y2 :%f " ,
			-(targetPositiveDimensions[2].data[0]-100.0),(-(targetPositiveDimensions[2].data[0])+128.0-100.0),
			-(targetPositiveDimensions[2].data[1])-200.0,(-(targetPositiveDimensions[2].data[1])+128.0-200.0));
	*/

	// The texture returns as pose the center of the trackable. The following if-statement simply checks that the tap is within this range
	if ((intersection.data[0] >= (buttonCoords[textureNumber][0] - 64))
			&& (intersection.data[0]
					<= (buttonCoords[textureNumber][0] + 64))
			&& (intersection.data[1]
					>= (buttonCoords[textureNumber][1] - 64))
			&& (intersection.data[1]
					<= (buttonCoords[textureNumber][1] + 64)))
		return true;
	else
		return false;

}


JNIEXPORT bool JNICALL
Java_com_newo_newoapp_activities_ScanActivity_isTapOnScreenInsideTarget(JNIEnv *env, jobject obj, jint target, jfloat x, jfloat y)
{

	if(isTapOnScreenInsideTexture(5,x,y) )
	{
		LOG("isTappedInRightPlace");

		// Handle to the activity class:
		jclass activityClass = env->GetObjectClass(obj);
		jmethodID getTextureMethodID = env->GetMethodID(activityClass,
		        "openWebSite", "(I)V");
		env->CallVoidMethod(obj, getTextureMethodID, 1);
	}else if(isTapOnScreenInsideTexture(6,x,y) )
	{
		LOG("isTappedInRightPlace");

		// Handle to the activity class:
		jclass activityClass = env->GetObjectClass(obj);
		jmethodID getTextureMethodID = env->GetMethodID(activityClass,
		        "openWebSite", "(I)V");
		env->CallVoidMethod(obj, getTextureMethodID, 2);

	}else if(isTapOnScreenInsideTexture(7,x,y) )
	{
		LOG("isTappedInRightPlace");

		// Handle to the activity class:
		jclass activityClass = env->GetObjectClass(obj);
		jmethodID getTextureMethodID = env->GetMethodID(activityClass,
		        "openWebSite", "(I)V");
		env->CallVoidMethod(obj, getTextureMethodID, 3);

	}


    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_isTapOnScreenInsideTarget");
    // Here we calculate that the touch event is inside the target
    QCAR::Vec3F intersection, lineStart, lineEnd;

    SampleMath::projectScreenPointToPlane(inverseProjMatrix, modelViewMatrix[target], screenWidth, screenHeight,
                              QCAR::Vec2F(x, y), QCAR::Vec3F(0, 0, 0), QCAR::Vec3F(0, 0, 1), intersection, lineStart, lineEnd);

    // The target returns as pose the center of the trackable. The following if-statement simply checks that the tap is within this range
    if ( (intersection.data[0] >= -(targetPositiveDimensions[target].data[0])) && (intersection.data[0] <= (targetPositiveDimensions[target].data[0])) &&
         (intersection.data[1] >= -(targetPositiveDimensions[target].data[1])) && (intersection.data[1] <= (targetPositiveDimensions[target].data[1])))
        return true;
    else
        return false;
}






// Multiply the UV coordinates by the given transformation matrix
void uvMultMat4f(float& transformedU, float& transformedV, float u, float v, float* pMat)
{
    float x = pMat[0]*u   +   pMat[4]*v /*+   pMat[ 8]*0.f */+ pMat[12]*1.f;
    float y = pMat[1]*u   +   pMat[5]*v /*+   pMat[ 9]*0.f */+ pMat[13]*1.f;
//  float z = pMat[2]*u   +   pMat[6]*v   +   pMat[10]*0.f   + pMat[14]*1.f;  // We dont need z and w so we comment them out
//  float w = pMat[3]*u   +   pMat[7]*v   +   pMat[11]*0.f   + pMat[15]*1.f;

    transformedU = x;
    transformedV = y;
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_setVideoDimensions(JNIEnv *env, jobject, jint target, jfloat videoWidth, jfloat videoHeight, jfloatArray textureCoordMatrix)
{
	LOG("Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_setVideoDimensions");

    // The quad originaly comes as a perfect square, however, the video
    // often has a different aspect ration such as 4:3 or 16:9,
    // To mitigate this we have two options:
    //    1) We can either scale the width (typically up)
    //    2) We can scale the height (typically down)
    // Which one to use is just a matter of preference. This example scales the height down.
    // (see the render call in Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlaybackRenderer_renderFrame)
    videoQuadAspectRatio[target] = videoHeight/videoWidth;

    jfloat *mtx = env->GetFloatArrayElements(textureCoordMatrix, 0);

    if (target == STONES)
    {
        uvMultMat4f(videoQuadTextureCoordsTransformedStones[0], videoQuadTextureCoordsTransformedStones[1], videoQuadTextureCoords[0], videoQuadTextureCoords[1], mtx);
        uvMultMat4f(videoQuadTextureCoordsTransformedStones[2], videoQuadTextureCoordsTransformedStones[3], videoQuadTextureCoords[2], videoQuadTextureCoords[3], mtx);
        uvMultMat4f(videoQuadTextureCoordsTransformedStones[4], videoQuadTextureCoordsTransformedStones[5], videoQuadTextureCoords[4], videoQuadTextureCoords[5], mtx);
        uvMultMat4f(videoQuadTextureCoordsTransformedStones[6], videoQuadTextureCoordsTransformedStones[7], videoQuadTextureCoords[6], videoQuadTextureCoords[7], mtx);
    }
    else //if (target == CHIPS)
    {
        uvMultMat4f(videoQuadTextureCoordsTransformedChips[0], videoQuadTextureCoordsTransformedChips[1], videoQuadTextureCoords[0], videoQuadTextureCoords[1], mtx);
        uvMultMat4f(videoQuadTextureCoordsTransformedChips[2], videoQuadTextureCoordsTransformedChips[3], videoQuadTextureCoords[2], videoQuadTextureCoords[3], mtx);
        uvMultMat4f(videoQuadTextureCoordsTransformedChips[4], videoQuadTextureCoordsTransformedChips[5], videoQuadTextureCoords[4], videoQuadTextureCoords[5], mtx);
        uvMultMat4f(videoQuadTextureCoordsTransformedChips[6], videoQuadTextureCoordsTransformedChips[7], videoQuadTextureCoords[6], videoQuadTextureCoords[7], mtx);
    }


    env->ReleaseFloatArrayElements(textureCoordMatrix, mtx, 0);
}




JNIEXPORT bool JNICALL
Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_isTracking(JNIEnv *, jobject, jint target)
{
	LOG("Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_isTracking");
    return isTracking[target];
}


void loadTexture(QCAR::Matrix44F modelViewMatrixButton, int currentTarget,int textureId, float translateX, float translateY)
{
	// The inacuracy of the rendering process in some devices means that
	// even if we use the "Less or Equal" version of the depth function
	// it is likely that we will get ugly artifacts
	// That is the translation in the Z direction is slightly different
	// Another posibility would be to use a depth func "ALWAYS" but
	// that is typically not a good idea

	 QCAR::Matrix44F modelViewProjectionButton;

	SampleUtils::translatePoseMatrix(translateX, translateY,
			targetPositiveDimensions[currentTarget].data[1] / 1.98f,
			&modelViewMatrixButton.data[0]);
	SampleUtils::scalePoseMatrix(
			(targetPositiveDimensions[currentTarget].data[1] / 2.0f),
			(targetPositiveDimensions[currentTarget].data[1] / 2.0f),
			(targetPositiveDimensions[currentTarget].data[1] / 2.0f),
			&modelViewMatrixButton.data[0]);
	SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
			&modelViewMatrixButton.data[0], &modelViewProjectionButton.data[0]);

	glUseProgram(keyframeShaderID);

	glVertexAttribPointer(keyframeVertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &quadVertices[0]);
	glVertexAttribPointer(keyframeNormalHandle, 3, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &quadNormals[0]);
	glVertexAttribPointer(keyframeTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &quadTexCoords[0]);

	glEnableVertexAttribArray(keyframeVertexHandle);
	glEnableVertexAttribArray(keyframeNormalHandle);
	glEnableVertexAttribArray(keyframeTexCoordHandle);

	glActiveTexture (GL_TEXTURE0);

	glBindTexture(GL_TEXTURE_2D, textures[textureId]->mTextureID);

	glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, GL_FALSE,
			(GLfloat*) &modelViewProjectionButton.data[0]);
	glUniform1i(keyframeTexSampler2DHandle, 0 /*GL_TEXTURE0*/);

	// Render
	glDrawElements(GL_TRIANGLES, NUM_QUAD_INDEX, GL_UNSIGNED_SHORT,
			(const GLvoid*) &quadIndices[0]);

	modelViewMatrixForTextures[textureId] = modelViewMatrixButton;

}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_renderFrame(JNIEnv * env, jobject obj)
{


	LOG("Successfully created new trackable render frame");

    //New code for Cloud Reco
    /*if (showStartScanButton)
    {
        jclass javaClass = env->GetObjectClass(obj);
        jmethodID method = env->GetMethodID(javaClass, "showStartScanButton", "()V");
        env->CallVoidMethod(obj, method);

        showStartScanButton = false;
    }*/
    ///////////////////


    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_GLRenderer_renderFrame");

    // Clear color and depth buffer
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Get the state from QCAR and mark the beginning of a rendering section
    QCAR::State state = QCAR::Renderer::getInstance().begin();

    // Explicitly render the Video Background
    QCAR::Renderer::getInstance().drawVideoBackground();

    glEnable(GL_DEPTH_TEST);

    // We must detect if background reflection is active and adjust the culling direction.
    // If the reflection is active, this means the post matrix has been reflected as well,
    // therefore standard counter clockwise face culling will result in "inside out" models.
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    if(QCAR::Renderer::getInstance().getVideoBackgroundConfig().mReflection == QCAR::VIDEO_BACKGROUND_REFLECTION_ON)
        glFrontFace(GL_CW);  //Front camera
    else
        glFrontFace(GL_CCW);   //Back camera


    for (int i=0; i<NUM_TARGETS; i++)
    {
        isTracking[i] = false;
        targetPositiveDimensions[i].data[0] = 0.0;
        targetPositiveDimensions[i].data[1] = 0.0;
    }

    // Did we find any trackables this frame?
    for(int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
    {
        // Get the trackable:
        const QCAR::TrackableResult* trackableResult = state.getTrackableResult(tIdx);

        const QCAR::ImageTarget& imageTarget = (const QCAR::ImageTarget&) trackableResult->getTrackable();

        int currentTarget;

        // We store the modelview matrix to be used later by the tap calculation
        if (strcmp(imageTarget.getName(), "stones") == 0)
            currentTarget=STONES;
        else
            currentTarget=CHIPS;

        modelViewMatrix[currentTarget] = QCAR::Tool::convertPose2GLMatrix(trackableResult->getPose());

        isTracking[currentTarget] = true;

        targetPositiveDimensions[currentTarget] = imageTarget.getSize();

        //LOG("imageSizeBefore X: %f Y: %f" ,targetPositiveDimensions[currentTarget].data[0] , targetPositiveDimensions[currentTarget].data[1] );


        // The pose delivers the center of the target, thus the dimensions
        // go from -width/2 to width/2, same for height
        targetPositiveDimensions[currentTarget].data[0] /= 2.0f;
        targetPositiveDimensions[currentTarget].data[1] /= 2.0f;


        // If the movie is ready to start playing or it has reached the end
        // of playback we render the keyframe
        if ((currentStatus[currentTarget] == READY) || (currentStatus[currentTarget] == REACHED_END) ||
            (currentStatus[currentTarget] == NOT_READY) || (currentStatus[currentTarget] == ERROR))
        {
            QCAR::Matrix44F modelViewMatrixKeyframe =
                QCAR::Tool::convertPose2GLMatrix(trackableResult->getPose());
            QCAR::Matrix44F modelViewProjectionKeyframe;
            SampleUtils::translatePoseMatrix(0.0f, 0.0f, targetPositiveDimensions[currentTarget].data[0],
                                                &modelViewMatrixKeyframe.data[0]);

            // Here we use the aspect ratio of the keyframe since it
            // is likely that it is not a perfect square

            float ratio=1.0;
            if (textures[currentTarget]->mSuccess)
                ratio = keyframeQuadAspectRatio[currentTarget];
            else
                ratio = targetPositiveDimensions[currentTarget].data[1] / targetPositiveDimensions[currentTarget].data[0];

            SampleUtils::scalePoseMatrix(targetPositiveDimensions[currentTarget].data[0],
                                         targetPositiveDimensions[currentTarget].data[0]*ratio,
                                         targetPositiveDimensions[currentTarget].data[0],
                                         &modelViewMatrixKeyframe.data[0]);
            SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                        &modelViewMatrixKeyframe.data[0] ,
                                        &modelViewProjectionKeyframe.data[0]);

            glUseProgram(keyframeShaderID);

            // Prepare for rendering the keyframe
            glVertexAttribPointer(keyframeVertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadVertices[0]);
            glVertexAttribPointer(keyframeNormalHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadNormals[0]);
            glVertexAttribPointer(keyframeTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadTexCoords[0]);

            glEnableVertexAttribArray(keyframeVertexHandle);
            glEnableVertexAttribArray(keyframeNormalHandle);
            glEnableVertexAttribArray(keyframeTexCoordHandle);

            glActiveTexture(GL_TEXTURE0);

            // The first loaded texture from the assets folder is the keyframe
            glBindTexture(GL_TEXTURE_2D, textures[currentTarget]->mTextureID);
            //glBindTexture(GL_TEXTURE_2D, textures[4]->mTextureID);


            glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, GL_FALSE,
                               (GLfloat*)&modelViewProjectionKeyframe.data[0] );
            glUniform1i(keyframeTexSampler2DHandle, 0 /*GL_TEXTURE0*/);

            // Render
            glDrawElements(GL_TRIANGLES, NUM_QUAD_INDEX, GL_UNSIGNED_SHORT,
                           (const GLvoid*) &quadIndices[0]);

            glDisableVertexAttribArray(keyframeVertexHandle);
            glDisableVertexAttribArray(keyframeNormalHandle);
            glDisableVertexAttribArray(keyframeTexCoordHandle);

            glUseProgram(0);
        }
        else // In any other case, such as playing or paused, we render the actual contents
        {
            QCAR::Matrix44F modelViewMatrixVideo =
                QCAR::Tool::convertPose2GLMatrix(trackableResult->getPose());
            QCAR::Matrix44F modelViewProjectionVideo;
            SampleUtils::translatePoseMatrix(0.0f, 0.0f, targetPositiveDimensions[currentTarget].data[0],
                                             &modelViewMatrixVideo.data[0]);

            // Here we use the aspect ratio of the video frame
            SampleUtils::scalePoseMatrix(targetPositiveDimensions[currentTarget].data[0],
                                         targetPositiveDimensions[currentTarget].data[0]*videoQuadAspectRatio[currentTarget],
                                         targetPositiveDimensions[currentTarget].data[0],
                                         &modelViewMatrixVideo.data[0]);
            SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                        &modelViewMatrixVideo.data[0] ,
                                        &modelViewProjectionVideo.data[0]);

            glUseProgram(videoPlaybackShaderID);

            // Prepare for rendering the keyframe
            glVertexAttribPointer(videoPlaybackVertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadVertices[0]);
            glVertexAttribPointer(videoPlaybackNormalHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadNormals[0]);

            if (strcmp(imageTarget.getName(), "stones") == 0)
                glVertexAttribPointer(videoPlaybackTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &videoQuadTextureCoordsTransformedStones[0]);
            else
                glVertexAttribPointer(videoPlaybackTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &videoQuadTextureCoordsTransformedChips[0]);


            glEnableVertexAttribArray(videoPlaybackVertexHandle);
            glEnableVertexAttribArray(videoPlaybackNormalHandle);
            glEnableVertexAttribArray(videoPlaybackTexCoordHandle);

            glActiveTexture(GL_TEXTURE0);

            // IMPORTANT:
            // Notice here that the texture that we are binding is not the
            // typical GL_TEXTURE_2D but instead the GL_TEXTURE_EXTERNAL_OES
            glBindTexture(GL_TEXTURE_EXTERNAL_OES, videoPlaybackTextureID[currentTarget]);
            glUniformMatrix4fv(videoPlaybackMVPMatrixHandle, 1, GL_FALSE,
                               (GLfloat*)&modelViewProjectionVideo.data[0]);
            glUniform1i(videoPlaybackTexSamplerOESHandle, 0 /*GL_TEXTURE0*/);

            // Render
            glDrawElements(GL_TRIANGLES, NUM_QUAD_INDEX, GL_UNSIGNED_SHORT,
                           (const GLvoid*) &quadIndices[0]);

            glDisableVertexAttribArray(videoPlaybackVertexHandle);
            glDisableVertexAttribArray(videoPlaybackNormalHandle);
            glDisableVertexAttribArray(videoPlaybackTexCoordHandle);

            glUseProgram(0);

        }

        // The following section renders the icons. The actual textures used
        // are loaded from the assets folder

        if ((currentStatus[currentTarget] == READY)  || (currentStatus[currentTarget] == REACHED_END) ||
            (currentStatus[currentTarget] == PAUSED) || (currentStatus[currentTarget] == NOT_READY)   ||
            (currentStatus[currentTarget] == ERROR))
        {
            // If the movie is ready to be played, pause, has reached end or is not
            // ready then we display one of the icons
            QCAR::Matrix44F modelViewMatrixButton =
                QCAR::Tool::convertPose2GLMatrix(trackableResult->getPose());
            QCAR::Matrix44F modelViewProjectionButton;

            glDepthFunc(GL_LEQUAL);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


            // The inacuracy of the rendering process in some devices means that
            // even if we use the "Less or Equal" version of the depth function
            // it is likely that we will get ugly artifacts
            // That is the translation in the Z direction is slightly different
            // Another posibility would be to use a depth func "ALWAYS" but
            // that is typically not a good idea
            SampleUtils::translatePoseMatrix(0.0f, 0.0f, targetPositiveDimensions[currentTarget].data[1]/1.98f,
                                             &modelViewMatrixButton.data[0]);
            SampleUtils::scalePoseMatrix((targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                         (targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                         (targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                         &modelViewMatrixButton.data[0]);
            SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                        &modelViewMatrixButton.data[0] ,
                                        &modelViewProjectionButton.data[0]);


            glUseProgram(keyframeShaderID);

            glVertexAttribPointer(keyframeVertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadVertices[0]);
            glVertexAttribPointer(keyframeNormalHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadNormals[0]);
            glVertexAttribPointer(keyframeTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
                                  (const GLvoid*) &quadTexCoords[0]);

            glEnableVertexAttribArray(keyframeVertexHandle);
            glEnableVertexAttribArray(keyframeNormalHandle);
            glEnableVertexAttribArray(keyframeTexCoordHandle);

            glActiveTexture(GL_TEXTURE0);

            // Depending on the status in which we are we choose the appropriate
            // texture to display. Notice that unlike the video these are regular
            // GL_TEXTURE_2D textures
            switch (currentStatus[currentTarget])
            {
                case READY:
                    glBindTexture(GL_TEXTURE_2D, textures[2]->mTextureID);
                    break;
                case REACHED_END:
                    glBindTexture(GL_TEXTURE_2D, textures[2]->mTextureID);
                    break;
                case PAUSED:
                    glBindTexture(GL_TEXTURE_2D, textures[2]->mTextureID);
                    break;
                case NOT_READY:
                    glBindTexture(GL_TEXTURE_2D, textures[3]->mTextureID);
                    break;
                case ERROR:
                    glBindTexture(GL_TEXTURE_2D, textures[4]->mTextureID);
                    break;
                default:
                    glBindTexture(GL_TEXTURE_2D, textures[3]->mTextureID);
                    break;
            }
            glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, GL_FALSE,
                               (GLfloat*)&modelViewProjectionButton.data[0] );
            glUniform1i(keyframeTexSampler2DHandle, 0 /*GL_TEXTURE0*/);

            // Render
            glDrawElements(GL_TRIANGLES, NUM_QUAD_INDEX, GL_UNSIGNED_SHORT,
                           (const GLvoid*) &quadIndices[0]);



            modelViewMatrixButton = QCAR::Tool::convertPose2GLMatrix(trackableResult->getPose());

            //LOG("imageSizeAfter X: %f Y: %f" ,targetPositiveDimensions[currentTarget].data[0] , targetPositiveDimensions[currentTarget].data[1] );


            loadTexture(modelViewMatrixButton,currentTarget,5,buttonCoords[5][0],buttonCoords[5][1]);

            loadTexture(modelViewMatrixButton,currentTarget,6,buttonCoords[6][0],buttonCoords[6][1]);

            loadTexture(modelViewMatrixButton,currentTarget,7,buttonCoords[7][0],buttonCoords[7][1]);


            /*


            // The inacuracy of the rendering process in some devices means that
                       // even if we use the "Less or Equal" version of the depth function
                       // it is likely that we will get ugly artifacts
                       // That is the translation in the Z direction is slightly different
                       // Another posibility would be to use a depth func "ALWAYS" but
                       // that is typically not a good idea
            		   modelViewMatrixButton =
                            QCAR::Tool::convertPose2GLMatrix(trackableResult->getPose());
                       SampleUtils::translatePoseMatrix(100.0f, -200.0f, targetPositiveDimensions[currentTarget].data[1]/1.98f,
                                                        &modelViewMatrixButton.data[0]);
                       SampleUtils::scalePoseMatrix(	(targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                                    (targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                                    (targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                                    &modelViewMatrixButton.data[0]);
                       SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                                   &modelViewMatrixButton.data[0] ,
                                                   &modelViewProjectionButton.data[0]);


                       glUseProgram(keyframeShaderID);

                       glVertexAttribPointer(keyframeVertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                             (const GLvoid*) &quadVertices[0]);
                       glVertexAttribPointer(keyframeNormalHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                             (const GLvoid*) &quadNormals[0]);
                       glVertexAttribPointer(keyframeTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
                                             (const GLvoid*) &quadTexCoords[0]);

                       glEnableVertexAttribArray(keyframeVertexHandle);
                       glEnableVertexAttribArray(keyframeNormalHandle);
                       glEnableVertexAttribArray(keyframeTexCoordHandle);

                       glActiveTexture(GL_TEXTURE0);

                       glBindTexture(GL_TEXTURE_2D, textures[5]->mTextureID);

                       glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, GL_FALSE,
                                          (GLfloat*)&modelViewProjectionButton.data[0] );
                       glUniform1i(keyframeTexSampler2DHandle, 0 /*GL_TEXTURE0*///);
                       /*
                       // Render
                       glDrawElements(GL_TRIANGLES, NUM_QUAD_INDEX, GL_UNSIGNED_SHORT,
                                      (const GLvoid*) &quadIndices[0]);





                       // The inacuracy of the rendering process in some devices means that
                                             // even if we use the "Less or Equal" version of the depth function
                                             // it is likely that we will get ugly artifacts
                                             // That is the translation in the Z direction is slightly different
                                             // Another posibility would be to use a depth func "ALWAYS" but
                                             // that is typically not a good idea
                                  		   modelViewMatrixButton =
                                                  QCAR::Tool::convertPose2GLMatrix(trackableResult->getPose());
                                             SampleUtils::translatePoseMatrix(-100.0f, -200.0f, targetPositiveDimensions[currentTarget].data[1]/1.98f,
                                                                              &modelViewMatrixButton.data[0]);
                                             SampleUtils::scalePoseMatrix((targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                                                          (targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                                                          (targetPositiveDimensions[currentTarget].data[1]/2.0f),
                                                                          &modelViewMatrixButton.data[0]);
                                             SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                                                         &modelViewMatrixButton.data[0] ,
                                                                         &modelViewProjectionButton.data[0]);


                                             glUseProgram(keyframeShaderID);

                                             glVertexAttribPointer(keyframeVertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                                                   (const GLvoid*) &quadVertices[0]);
                                             glVertexAttribPointer(keyframeNormalHandle, 3, GL_FLOAT, GL_FALSE, 0,
                                                                   (const GLvoid*) &quadNormals[0]);
                                             glVertexAttribPointer(keyframeTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
                                                                   (const GLvoid*) &quadTexCoords[0]);

                                             glEnableVertexAttribArray(keyframeVertexHandle);
                                             glEnableVertexAttribArray(keyframeNormalHandle);
                                             glEnableVertexAttribArray(keyframeTexCoordHandle);

                                             glActiveTexture(GL_TEXTURE0);

                                             glBindTexture(GL_TEXTURE_2D, textures[6]->mTextureID);

                                             glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, GL_FALSE,
                                                                (GLfloat*)&modelViewProjectionButton.data[0] );
                                             glUniform1i(keyframeTexSampler2DHandle, 0 /*GL_TEXTURE0*///);

                                             // Render
                                             /*glDrawElements(GL_TRIANGLES, NUM_QUAD_INDEX, GL_UNSIGNED_SHORT,
                                                            (const GLvoid*) &quadIndices[0]);
											*/




            glDisableVertexAttribArray(keyframeVertexHandle);
            glDisableVertexAttribArray(keyframeNormalHandle);
            glDisableVertexAttribArray(keyframeTexCoordHandle);

            glUseProgram(0);

            // Finally we return the depth func to its original state
            glDepthFunc(GL_LESS);
            glDisable(GL_BLEND);
        }

        SampleUtils::checkGlError("VideoPlayback renderFrame");
    }

    glDisable(GL_DEPTH_TEST);

    QCAR::Renderer::getInstance().end();
}



void
configureVideoBackground()
{
    // Get the default video mode:
    QCAR::CameraDevice& cameraDevice = QCAR::CameraDevice::getInstance();
    QCAR::VideoMode videoMode = cameraDevice.
                                getVideoMode(QCAR::CameraDevice::MODE_DEFAULT);


    // Configure the video background
    QCAR::VideoBackgroundConfig config;
    config.mEnabled = true;
    config.mSynchronous = true;
    config.mPosition.data[0] = 0.0f;
    config.mPosition.data[1] = 0.0f;

    if (isActivityInPortraitMode)
    {
        LOG("configureVideoBackground PORTRAIT");
        config.mSize.data[0] = videoMode.mHeight
                                * (screenHeight / (float)videoMode.mWidth);
        config.mSize.data[1] = screenHeight;

        if(config.mSize.data[0] < screenWidth)
        {
            LOG("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
            config.mSize.data[0] = screenWidth;
            config.mSize.data[1] = screenWidth *
                              (videoMode.mWidth / (float)videoMode.mHeight);
        }
    }
    else
    {
        LOG("configureVideoBackground LANDSCAPE");
        config.mSize.data[0] = screenWidth;
        config.mSize.data[1] = videoMode.mHeight
                            * (screenWidth / (float)videoMode.mWidth);

        if(config.mSize.data[1] < screenHeight)
        {
            LOG("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
            config.mSize.data[0] = screenHeight
                                * (videoMode.mWidth / (float)videoMode.mHeight);
            config.mSize.data[1] = screenHeight;
        }
    }

    LOG("Configure Video Background : Video (%d,%d), Screen (%d,%d), mSize (%d,%d)", videoMode.mWidth, videoMode.mHeight, screenWidth, screenHeight, config.mSize.data[0], config.mSize.data[1]);

    // Set the config:
    QCAR::Renderer::getInstance().setVideoBackgroundConfig(config);
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_initApplicationNative(
                            JNIEnv* env, jobject obj, jint width, jint height)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_initApplicationNative");

    // Store screen dimensions
    screenWidth = width;
    screenHeight = height;

    // Handle to the activity class:
    jclass activityClass = env->GetObjectClass(obj);

    jmethodID getTextureCountMethodID = env->GetMethodID(activityClass,
                                                    "getTextureCount", "()I");
    if (getTextureCountMethodID == 0)
    {
        LOG("Function getTextureCount() not found.");
        return;
    }

    textureCount = env->CallIntMethod(obj, getTextureCountMethodID);
    if (!textureCount)
    {
        LOG("getTextureCount() returned zero.");
        return;
    }

    textures = new Texture*[textureCount];


    // Initialize Java mathods to be called from Native code


    jmethodID getTextureMethodID = env->GetMethodID(activityClass,
        "getTexture", "(I)Lcom/newo/newoapp/recognition/Texture;");

    createProductTextureID = env->GetMethodID(activityClass,
    		"createProductTexture", "(Ljava/lang/String;)V");






    if (getTextureMethodID == 0)
    {
        LOG("Function getTexture() not found.");
        return;
    }

    // Register the textures
    for (int i = 0; i < textureCount; ++i)
    {

        jobject textureObject = env->CallObjectMethod(obj, getTextureMethodID, i);
        if (textureObject == NULL)
        {
            LOG("GetTexture() returned zero pointer");
            return;
        }

        textures[i] = Texture::create(env, textureObject);
    }

    activityObj = env->NewGlobalRef(obj);

    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_initApplicationNative finished");
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_deinitApplicationNative(
                                                        JNIEnv* env, jobject obj)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_deinitApplicationNative");

    // Release texture resources
    if (textures != 0)
    {
        for (int i = 0; i < textureCount; ++i)
        {
            delete textures[i];
            textures[i] = NULL;
        }

        delete[]textures;
        textures = NULL;

        textureCount = 0;
    }
}




JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_startCamera(JNIEnv *,
                                                                         jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_startCamera");

    // Initialize the camera:
    if (!QCAR::CameraDevice::getInstance().init())
        return;



    // Configure the video background
    configureVideoBackground();

    // Select the default mode:
    if (!QCAR::CameraDevice::getInstance().selectVideoMode(
                                QCAR::CameraDevice::MODE_DEFAULT))
        return;

    QCAR::setHint(QCAR::HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 2);

    // Start the camera:
    if (!QCAR::CameraDevice::getInstance().start())
        return;

    // Start the tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    //QCAR::Tracker* objectTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);
    QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                                                                        trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));
    if(objectTracker != 0)
        objectTracker->start();

    // Start cloud based recognition if we are in scanning mode:
    if (scanningMode)
    {
        QCAR::TargetFinder* targetFinder = objectTracker->getTargetFinder();
        assert (targetFinder != 0);

        LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_started");

        targetFinder->startRecognition();
    }

}




JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_stopCamera(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_stopCamera");

    // Stop the tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    //QCAR::Tracker* objectTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);

    QCAR::ObjectTracker* objectTracker = static_cast<QCAR::ObjectTracker*>(
                                                                       trackerManager.getTracker( QCAR::ObjectTracker::getClassType() ));

    if(objectTracker != 0)
        objectTracker->stop();

    // Stop Cloud Reco:
    QCAR::TargetFinder* targetFinder = objectTracker->getTargetFinder();
    assert (targetFinder != 0);

    targetFinder->stop();

    // Clears the trackables
    targetFinder->clearTrackables();

    QCAR::CameraDevice::getInstance().stop();
    QCAR::CameraDevice::getInstance().deinit();

    // Reset the state variables for Cloud Reco
    initStateVariables();
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_activities_ScanActivity_setProjectionMatrix(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlayback_setProjectionMatrix");

    // Cache the projection matrix:
    const QCAR::CameraCalibration& cameraCalibration =
                                QCAR::CameraDevice::getInstance().getCameraCalibration();
    projectionMatrix = QCAR::Tool::getProjectionGL(cameraCalibration, 2.0f, 2500.0f);

    inverseProjMatrix = SampleMath::Matrix44FInverse(projectionMatrix);
}


JNIEXPORT jboolean JNICALL
Java_com_newo_newoapp_activities_ScanActivity_autofocus(JNIEnv*, jobject)
{
	LOG("Java_com_newo_newoapp_activities_ScanActivity_autofocus");
    return QCAR::CameraDevice::getInstance().setFocusMode(QCAR::CameraDevice::FOCUS_MODE_TRIGGERAUTO) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_newo_newoapp_activities_ScanActivity_setFocusMode(JNIEnv*, jobject, jint mode)
{
	LOG("Java_com_newo_newoapp_activities_ScanActivity_setFocusMode");

    int qcarFocusMode;

    switch ((int)mode)
    {
        case 0:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_NORMAL;
            break;

        case 1:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_CONTINUOUSAUTO;
            break;

        case 2:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_INFINITY;
            break;

        case 3:
            qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_MACRO;
            break;

        default:
            return JNI_FALSE;
    }

    return QCAR::CameraDevice::getInstance().setFocusMode(qcarFocusMode) ? JNI_TRUE : JNI_FALSE;
}


JNIEXPORT void JNICALL
Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_initRendering(
                                                    JNIEnv* env, jobject obj)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlaybackRenderer_initRendering");

    // Define clear color
    glClearColor(0.0f, 0.0f, 0.0f, QCAR::requiresAlpha() ? 0.0f : 1.0f);

    // Now generate the OpenGL texture objects and add settings
    for (int i = 0; i < textureCount; ++i)
    {
        // Here we create the textures for the keyframe
        // and for all the icons
        glGenTextures(1, &(textures[i]->mTextureID));
        glBindTexture(GL_TEXTURE_2D, textures[i]->mTextureID);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textures[i]->mWidth,
                textures[i]->mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                (GLvoid*)  textures[i]->mData);
    }

    // Now we create the texture for the video data from the movie
    // IMPORTANT:
    // Notice that the textures are not typical GL_TEXTURE_2D textures
    // but instead are GL_TEXTURE_EXTERNAL_OES extension textures
    // This is required by the Android SurfaceTexture
    for (int i=0; i<NUM_TARGETS; i++)
    {
        glGenTextures(1, &videoPlaybackTextureID[i]);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, videoPlaybackTextureID[i]);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
    }

    // The first shader is the one that will display the video data of the movie
    // (it is aware of the GL_TEXTURE_EXTERNAL_OES extension)
    videoPlaybackShaderID           = SampleUtils::createProgramFromBuffer(
                                                videoPlaybackVertexShader,
                                                videoPlaybackFragmentShader);
    videoPlaybackVertexHandle       = glGetAttribLocation(videoPlaybackShaderID,
                                                "vertexPosition");
    videoPlaybackNormalHandle       = glGetAttribLocation(videoPlaybackShaderID,
                                                "vertexNormal");
    videoPlaybackTexCoordHandle     = glGetAttribLocation(videoPlaybackShaderID,
                                                "vertexTexCoord");
    videoPlaybackMVPMatrixHandle    = glGetUniformLocation(videoPlaybackShaderID,
                                                "modelViewProjectionMatrix");
    videoPlaybackTexSamplerOESHandle = glGetUniformLocation(videoPlaybackShaderID,
                                                "texSamplerOES");

    // This is a simpler shader with regular 2D textures
    keyframeShaderID                = SampleUtils::createProgramFromBuffer(
                                                keyframeVertexShader,
                                                keyframeFragmentShader);
    keyframeVertexHandle            = glGetAttribLocation(keyframeShaderID,
                                                "vertexPosition");
    keyframeNormalHandle            = glGetAttribLocation(keyframeShaderID,
                                                "vertexNormal");
    keyframeTexCoordHandle          = glGetAttribLocation(keyframeShaderID,
                                                "vertexTexCoord");
    keyframeMVPMatrixHandle         = glGetUniformLocation(keyframeShaderID,
                                                "modelViewProjectionMatrix");
    keyframeTexSampler2DHandle      = glGetUniformLocation(keyframeShaderID,
                                                "texSampler2D");

    keyframeQuadAspectRatio[STONES] = (float)textures[0]->mHeight / (float)textures[0]->mWidth;
    keyframeQuadAspectRatio[CHIPS]  = (float)textures[1]->mHeight / (float)textures[1]->mWidth;

}



JNIEXPORT void JNICALL
Java_com_newo_newoapp_recognition_templates_videoplayback_VideoPlaybackRenderer_updateRendering(
                        JNIEnv* env, jobject obj, jint width, jint height)
{
    LOG("Java_com_qualcomm_QCARSamples_VideoPlayback_VideoPlaybackRenderer_updateRendering");

    // Update screen dimensions
    screenWidth = width;
    screenHeight = height;

    // Reconfigure the video background
    configureVideoBackground();
}



JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved)
{
    LOG("JNI_OnLoad");
    javaVM = vm;
    return JNI_VERSION_1_4;
}



#ifdef __cplusplus
}
#endif
