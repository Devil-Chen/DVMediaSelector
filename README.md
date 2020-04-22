# DVMediaSelector
[![](https://jitpack.io/v/Devil-Chen/DVMediaSelector.svg)](https://jitpack.io/#Devil-Chen/DVMediaSelector)  
  
Android媒体资源选择库（支持图片/视频/仿微信拍照、拍视频），非常简单使用，支持图库多选、单选、仿微信拍照拍视频、系统照相机拍照拍视频。

## 预览
![拍摄](https://github.com/Devil-Chen/DVMediaSelector/blob/master/screenshot/take_photo.png)  
![单选](https://github.com/Devil-Chen/DVMediaSelector/blob/master/screenshot/single_select.png) 
![预览大图](https://github.com/Devil-Chen/DVMediaSelector/blob/master/screenshot/preview.png)  
![多选](https://github.com/Devil-Chen/DVMediaSelector/blob/master/screenshot/multiple_select.png)  
![选择文件夹](https://github.com/Devil-Chen/DVMediaSelector/blob/master/screenshot/folder_select.png)  

## 依赖
**根bulid.gradle添加**
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
**在项目bulid.gradle添加**
```
dependencies {
    implementation 'com.github.Devil-Chen:DVMediaSelector:1.1.0'
}
```

## 使用
**使用之前先自定义一个ImageLoader，必须在调用选择文件之前调用此方法**
```
//可使用Glide、Picasso等方式加载，由调用者自己决定
//设置加载器
MediaSelectorManager.getInstance().initImageLoader(new ImageLoader() {
    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }
});
```

**多选**
```
//最简单的调用
MediaSelectorManager.openSelectMediaWithConfig(this, MediaSelectorManager.getDefaultListConfigBuilder().build(), new OnSelectMediaListener() {
    @Override
    public void onSelectMedia(List<String> li_path) {
        for (String path : li_path) {
            tvResult.append(path + "\n");
        }
    }
});

//自定义配置调用
DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
//是否多选
.multiSelect(true)
//最大选择数量
.maxNum(9)
//最小选择数量
.minNum(2)
//设置选中图标
.checkIconResource(R.mipmap.icon_dv_checked)
//设置非选中图标
.unCheckIconResource(R.mipmap.icon_dv_unchecked)
// 使用沉浸式状态栏
.statusBarColor(Color.BLUE)
//每行显示的数量
.listSpanCount(3)
//状态栏的mode
.statusBarLightMode(true)
//.statusBarDrakMode(true)
//设置选择资源的类型
.mediaType(DVMediaType.ALL)
//设置返回图标
//.backResourceId(R.mipmap.icon_back)
//设置右边标题
.rigntTitleText("所有图片")
//设置右边标题文字颜色
.rightTitleTextColor(Color.WHITE)
//是否显示右边标题
.rightTitleVisibility(View.VISIBLE)
//设置标题文字
.title("资源选择")
//设置标题文字颜色
.titleTextColor(Color.WHITE)
//设置标题背景颜色
.titleBgColor(Color.BLUE)
//确定按钮文字
.sureBtnText("确定")
//确定按钮文字颜色
.sureBtnTextColor(Color.WHITE)
//确定按钮背景色（与Resource只能选择一种）
//.sureBtnBgColor(Color.BLUE)
//确定按钮所在布局背景色（与color只能选择一种）
.sureBtnBgResource(R.drawable.shape_btn_default)
//设置文件临时缓存路径
.fileCachePath(FileUtils.createRootPath(this))
//设置是否包含预览
.hasPreview(true)
//是否需要快速加载视频缩略图（默认为true从系统直接获取，获取不到使用加载图片框架获取首帧。为false，直接使用加载图片框架获取首帧）
.quickLoadVideoThumb(true)
.build();

//打开界面
MediaSelectorManager.openSelectMediaWithConfig(this, config, new OnSelectMediaListener() {
    @Override
    public void onSelectMedia(List<String> li_path) {
        for (String path : li_path) {
            tvResult.append(path + "\n");
        }
    }
});
```
**单选**
```
DVListConfig config = MediaSelectorManager.getDefaultListConfigBuilder()
// 是否多选
.multiSelect(false)
//第一个菜单是否显示照相机
.needCamera(true)
//第一个菜单显示照相机的图标
.cameraIconResource(R.mipmap.ic_launcher)
//每行显示的数量
.listSpanCount(4)
// 确定按钮文字颜色
.sureBtnTextColor(Color.WHITE)
// 使用沉浸式状态栏
.statusBarColor(Color.parseColor("#3F51B5"))
// 返回图标ResId
.backResourceId(R.mipmap.icon_back2)
//标题背景
.titleBgColor(Color.parseColor("#3F51B5"))
//是否需要裁剪
.needCrop(true)
//裁剪大小
.cropSize(1, 1, 200, 200)
.build();

MediaSelectorManager.openSelectMediaWithConfig(this, config, new OnSelectMediaListener() {
@Override
public void onSelectMedia(List<String> li_path) {
    for (String path : li_path) {
        tvResult.append(path + "\n");
    }
}
});
```

**照相机**
```
DVCameraConfig config = MediaSelectorManager.getDefaultCameraConfigBuilder()
        //是否使用系统照相机（默认使用仿微信照相机）
        .isUseSystemCamera(false)
        //是否需要裁剪
        .needCrop(true)
        //裁剪大小
        .cropSize(1, 1, 200, 200)
        //媒体类型（如果是使用系统照相机，必须指定DVMediaType.PHOTO或DVMediaType.VIDEO）
        .mediaType(DVMediaType.ALL)
        //设置录制时长
        .maxDuration(10)
        //闪光灯是否启用
        .flashLightEnable(true)
        .build();

MediaSelectorManager.openCameraWithConfig(this, config, new OnSelectMediaListener() {
    @Override
    public void onSelectMedia(List<String> li_path) {
        for (String path : li_path) {
            tvResult.append(path + "\n");
        }
    }
});
```

## 参考
[https://github.com/CJT2325/CameraView](https://github.com/CJT2325/CameraView)
