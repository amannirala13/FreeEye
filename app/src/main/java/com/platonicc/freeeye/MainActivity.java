package com.platonicc.freeeye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.internal.RtcEngineEvent;
import io.agora.rtc.video.VideoCanvas;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler mRtcEventHandler;

    private int UID;

    private FrameLayout mLocalContainer, mRemoteContainer;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRtcEngine.leaveChannel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocalContainer = findViewById(R.id.localVideo);
        mRemoteContainer = findViewById(R.id.remoteVideo);

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
        }
            mRtcEventHandler = new IRtcEngineEventHandler() {

                @Override
                public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
                    super.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
                    UID = uid;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Yeah", Toast.LENGTH_SHORT).show();
                            setupRemoteVideo();
                        }
                    });
                }

                @Override
                public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
                    super.onFirstRemoteVideoFrame(uid, width, height, elapsed);

                }
            };
            initializeEngine();
        }

    private void setVideoProfile() {
        mRtcEngine.enableAudio();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_240P_3,false);
    }

    private void joinChannel() {

        // Join a channel with a token.
        mRtcEngine.joinChannel("006c4ac67cce2574b27aff5f58cb19a48f9IAAj/Y6XXCo8giXjM1g6POsULz+vNMUXD3V1GNrHHp9qsfnb4lUAAAAAEACONz9pg8vsXQEAAQCDy+xd", "Testasp", "Extra Optional Data", UID);
    }

    private void setupLocalVideo() {
        // Enable the video module.
        mRtcEngine.enableVideo();

        // Create a SurfaceView object.
        SurfaceView mLocalView;

        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Set the local video view.
        Toast.makeText(this, String.valueOf(UID), Toast.LENGTH_SHORT).show();
        VideoCanvas localVideoCanvas = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, UID);
        mRtcEngine.setupLocalVideo(localVideoCanvas);
    }

    // Listen for the onFirstRemoteVideoDecoded callback.
    // This callback occurs when the first video frame of a remote user is received and decoded after the remote user successfully joins the channel.
    // You can call the setupRemoteVideo method in this callback to set up the remote video view.
    private void setupRemoteVideo() {

        // Create a SurfaceView object.
        SurfaceView mRemoteView;


        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        // Set the remote video view.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, UID));

    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }return true;
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.app_id), mRtcEventHandler);
            joinChannel();
            setupLocalVideo();
            setVideoProfile();
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
