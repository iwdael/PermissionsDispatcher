# PermissionsDispatcher
![](https://img.shields.io/badge/platform-android-orange.svg)
![](https://img.shields.io/badge/language-java-yellow.svg)
![](https://jitpack.io/v/com.iwdael/permissionsdispatcher.svg)
![](https://img.shields.io/badge/build-passing-brightgreen.svg)
![](https://img.shields.io/badge/license-apache--2.0-green.svg)
![](https://img.shields.io/badge/api-19+-green.svg)

PermissionsDispatcher用于授权的手机的操作系统是Android 6及以上,只需要简单地配置项目，就可以实现动态授权。
## 使用说明
权限申请仅支持Activity或者Fragment，通过注解方式动态生成额外的代码，整个申请流程无反射。
|注解|功能|值|必选|
|:------:|:------:|:------:|:------:|
|PermissionsDispatcher|需要申请的Activity或Fragment|无|是|
|PermissionsDispatcherNeeds|申请权限成功调用|权限，标识|是|
|PermissionsDispatcherDenied|权限申请失败调用|权限，标识|否|
|PermissionsDispatcherRationale|权限开始申请时，调用，可用于向用户展示自定义弹窗|权限，标识|否|
### 代码示例
推荐使用kotlin
```
@PermissionsDispatcher
class MainActivity : AppCompatActivity() {
    //自动生成 fun takePhotoWithPermission(path: String, name: String)
    //应该避免直接调用takePhoto
    //通过调用takePhotoWithPermission，动态申请权限，若未申请权限，则自动申请，申请成功会自动回调takePhoto，反之回调takePhotoDenied
    @PermissionsDispatcherNeeds(Manifest.permission.CAMERA)
    fun takePhoto(path: String, name: String) {}

    //此方法可省略，省略后无法收到权限申请失败
    //permission 申请失败的权限
    //bannedResults 被禁止的权限
    //permission与bannedResults 一一对应
    @PermissionsDispatcherDenied(Manifest.permission.CAMERA)
    fun takePhotoDenied(permission: List<String>, bannedResults: List<Boolean>) {}

    //此方法可省略
    //此方法主要作用：向用户解释申请该权限的目的
    //用户同意则执行rationale.apply()，继续申请权限。
    //用户拒绝则执行rationale.deny()，中断后续申请，且不会回调takePhotoDenied
    @PermissionsDispatcherRationale(Manifest.permission.CAMERA)
    fun takePhotoRationale(rationale: PermissionsRationale) {}
}
```
不推荐使用java
动态生成的代理方法未：MasterActivityDispatcher.takePhotoWithPermission(MasterActivity target, String path,String name)
```
@PermissionsDispatcher
public class MasterActivity extends AppCompatActivity {
    @PermissionsDispatcherNeeds(Manifest.permission.CAMERA)
    public void takePhoto(String path, String name) {}

    @PermissionsDispatcherDenied(Manifest.permission.CAMERA)
    public void takePhotoDenied(List<String> permission, List<Boolean> bannedResults) {}

    @PermissionsDispatcherRationale(Manifest.permission.CAMERA)
    public void takePhotoRationale(PermissionsRationale rationale) {}
}
```

#### 快速引入项目 
```Java
	dependencies {
                ...
	        compile 'com.iwdael.permissionsdispatcher:dispatcher:$version'
	        kapt 'com.iwdael.permissionsdispatcher:compiler:$version'
	}
```
<br><br>