package com.devil.library.camera.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import com.devil.library.camera.listener.FocusCallback;
import com.devil.library.camera.listener.OnFrameAvailableListener;
import com.devil.library.camera.listener.OnSurfaceTextureListener;
import com.devil.library.camera.listener.PreviewCallback;
import com.devil.library.camera.params.CalculateType;
import com.devil.library.camera.params.CameraParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 相机预览控制器
 */
public class CameraController implements Camera.PreviewCallback {
    // 16:9的默认宽高(理想值)
    private static final int DEFAULT_16_9_WIDTH = 1280;
    private static final int DEFAULT_16_9_HEIGHT = 720;

    // 期望的fps
    private int mExpectFps = CameraParam.DESIRED_PREVIEW_FPS;
    // 预览宽度
    private int mPreviewWidth = DEFAULT_16_9_WIDTH;
    // 预览高度
    private int mPreviewHeight = DEFAULT_16_9_HEIGHT;
    // 预览角度
    private int mOrientation;
    // 相机对象
    private Camera mCamera;
    // 摄像头id
    private int mCameraId;
    // SurfaceTexture成功回调
    private OnSurfaceTextureListener mSurfaceTextureListener;
    // 预览数据回调
    private PreviewCallback mPreviewCallback;
    // 输出纹理更新回调
    private OnFrameAvailableListener mFrameAvailableListener;
    // 相机输出的SurfaceTexture
    private SurfaceTexture mOutputTexture;
    private HandlerThread mOutputThread;
    // 上下文
    private final Activity mActivity;
    //是否正在停止中
    private boolean isStopping = false;

    public CameraController(@NonNull Activity activity) {
        mActivity = activity;
        //默认开启后置摄像头
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//        mCameraId = CameraApi.hasFrontCamera(activity) ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    /**
     * 设置显示
     * @param surface
     */
    public void setSurface(SurfaceTexture surface) {
        this.mOutputTexture = surface;
    }
    public void setOnSurfaceTextureListener(OnSurfaceTextureListener listener) {
        mSurfaceTextureListener = listener;
    }
    public void setPreviewCallback(PreviewCallback callback) {
        mPreviewCallback = callback;
    }
    public void setOnFrameAvailableListener(OnFrameAvailableListener listener) {
        mFrameAvailableListener = listener;
    }

    /**
     * 开启相机
     */
    public void openCamera() {
        closeCamera();
        if (mCamera != null) {
            throw new RuntimeException("camera already initialized!");
        }
        try {
            mCamera = Camera.open(mCameraId);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Camera","Unable to open camera:" + e.toString());
            if (mActivity != null) mActivity.finish();
            return;
        }
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }
        CameraParam cameraParam = CameraParam.getInstance();
        cameraParam.cameraId = mCameraId;
        Camera.Parameters parameters = mCamera.getParameters();
        cameraParam.supportFlash = CameraApi.checkSupportFlashLight(parameters);
        cameraParam.previewFps = CameraApi.chooseFixedPreviewFps(parameters, mExpectFps * 1000);
        parameters.setRecordingHint(true);
        // 后置摄像头自动对焦
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK
                && CameraApi.supportAutoFocusFeature(parameters)) {
            mCamera.cancelAutoFocus();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(parameters);
        setPreviewSize(mCamera, mPreviewWidth, mPreviewHeight);
        setPictureSize(mCamera, mPreviewWidth, mPreviewHeight);
        mOrientation = calculateCameraPreviewOrientation(mActivity);
        mCamera.setDisplayOrientation(mOrientation);
        releaseSurfaceTexture();
        mOutputTexture = createDetachedSurfaceTexture();
        try {
            mCamera.setPreviewTexture(mOutputTexture);
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        if (mSurfaceTextureListener != null) {
            mSurfaceTextureListener.onSurfaceTexturePrepared(mOutputTexture);
        }
    }

    /**
     * 设置拍摄的照片大小
     * @param camera
     * @param expectWidth
     * @param expectHeight
     */
    private void setPictureSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = CameraApi.calculatePerfectSize(parameters.getSupportedPictureSizes(),
                expectWidth, expectHeight, CalculateType.Max);
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    /**
     * 设置预览大小
     * @param camera
     * @param expectWidth
     * @param expectHeight
     */
    private void setPreviewSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = CameraApi.calculatePerfectSize(parameters.getSupportedPreviewSizes(),
                expectWidth, expectHeight, CalculateType.Lower);
        parameters.setPreviewSize(size.width, size.height);
        mPreviewWidth = size.width;
        mPreviewHeight = size.height;
        camera.setParameters(parameters);
    }

    /**
     * 创建一个SurfaceTexture并
     * @return
     */
    private SurfaceTexture createDetachedSurfaceTexture() {
        // 创建一个新的SurfaceTexture并从解绑GL上下文
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        surfaceTexture.detachFromGLContext();
        if (Build.VERSION.SDK_INT >= 21) {
            if (mOutputThread != null) {
                mOutputThread.quit();
                mOutputThread = null;
            }
            mOutputThread = new HandlerThread("FrameAvailableThread");
            mOutputThread.start();
            surfaceTexture.setOnFrameAvailableListener(texture -> {
                if (mFrameAvailableListener != null) {
                    mFrameAvailableListener.onFrameAvailable(texture);
                }
            }, new Handler(mOutputThread.getLooper()));
        } else {
            surfaceTexture.setOnFrameAvailableListener(texture -> {
                if (mFrameAvailableListener != null) {
                    mFrameAvailableListener.onFrameAvailable(texture);
                }
            });
        }
        return surfaceTexture;
    }

    /**
     * 释放资源
     */
    private void releaseSurfaceTexture() {
        if (mOutputTexture != null) {
            mOutputTexture.release();
            mOutputTexture = null;
        }
        if (mOutputThread != null) {
            mOutputThread.quitSafely();
            mOutputThread = null;
        }
    }

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.addCallbackBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        releaseSurfaceTexture();
    }

    /**
     * 设置预览角度，setDisplayOrientation本身只能改变预览的角度
     * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的
     * 拍摄的照片需要自行处理
     * 这里Nexus5X的相机简直没法吐槽，后置摄像头倒置了，切换摄像头之后就出现问题了。
     * @param activity
     */
    private int calculateCameraPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CameraParam.getInstance().cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * 切换相机
     */
    public void switchCamera() {
        boolean front = !isFront();
        front = front && CameraApi.hasFrontCamera(mActivity);
        // 期望值不一致
        if (front != isFront()) {
            setFront(front);
            openCamera();
        }
    }

    /**
     * 设置是否为前置摄像头
     * @param front 是否前置摄像头
     */
    public void setFront(boolean front) {
        if (front) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    /**
     * 是否前置摄像头
     */
    public boolean isFront() {
        return (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * 获取预览Surface的旋转角度
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * 获取预览宽度
     */
    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    /**
     * 获取预览高度
     */
    public int getPreviewHeight() {
        return mPreviewHeight;
    }

    /**
     * 是否可以自动对焦
     * @return
     */
    public boolean canAutoFocus() {
        List<String> focusModes = mCamera.getParameters().getSupportedFocusModes();
        return (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO));
    }
    /**
     * 设置对焦区域
     * @param rect 对焦区域
     */
    public void setFocusArea(Rect rect) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters(); // 先获取当前相机的参数配置对象
            if (CameraApi.supportAutoFocusFeature(parameters)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO); // 设置聚焦模式
            }
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(rect, CameraParam.Weight));
                // 设置聚焦区域
                if (parameters.getMaxNumFocusAreas() > 0) {
                    parameters.setFocusAreas(focusAreas);
                }
                // 设置计量区域
                if (parameters.getMaxNumMeteringAreas() > 0) {
                    parameters.setMeteringAreas(focusAreas);
                }
                // 取消掉进程中所有的聚焦功能
                mCamera.setParameters(parameters);
                mCamera.autoFocus((success, camera) -> {
                    Camera.Parameters params = camera.getParameters();
                    // 设置自动对焦
                    if (CameraApi.supportAutoFocusFeature(params)) {
                        camera.cancelAutoFocus();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }
                    camera.setParameters(params);
                    camera.autoFocus(null);
                });
            }
        }
    }
    /**
     * 获取对焦区域
     */
    public Rect getFocusArea(float x, float y, int width, int height, int focusSize) {
        return CameraApi.calculateTapArea(x, y, width, height, focusSize, 1.0f);
    }

    private  int handlerTime = 0;
    /**
     * 处理对焦
     */
    public  void handleFocus(final Context context, final float x, final float y, final FocusCallback callback) {
        if (mCamera == null) {
            return;
        }
        final Camera.Parameters params = mCamera.getParameters();
        Rect focusRect = CameraApi.calculateTapArea(x, y, 1f, context);
        mCamera.cancelAutoFocus();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            callback.focusSuccess();
            return;
        }
        final String currentFocusMode = params.getFocusMode();
        try {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(params);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success || handlerTime > 10) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(currentFocusMode);
                        camera.setParameters(params);
                        handlerTime = 0;
                        if (callback != null)callback.focusSuccess();
                    } else {
                        handlerTime++;
                        handleFocus(context, x, y, callback);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否支持闪光灯
     * @param front 是否前置摄像头
     */
    public boolean isSupportFlashLight(boolean front) {
        if (front) {
            return false;
        }
        return CameraApi.checkSupportFlashLight(mCamera);
    }

    /**
     * 设置闪光灯
     * @param on 是否打开闪光灯
     */
    public void setFlashLight(boolean on) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (on) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 设置闪光灯状态
     * @param flashMode
     */
    public void setFlashMode(String flashMode) {
        if (mCamera == null)
            return;
        try{
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(flashMode);
            mCamera.setParameters(params);
        }catch(Exception e){
            Log.e("Camera",""+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mPreviewCallback != null) {
            mPreviewCallback.onPreviewFrame(data);
        }
    }

    /**
     * 重新开始预览
     */
    public void doStartPreview(){
        if (mCamera == null || !isStopping) return;
        try {
            mCamera.setPreviewTexture(mOutputTexture);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
            isStopping = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止预览
     */
    public void doStopPreview() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isStopping = true;
        }
    }
}
