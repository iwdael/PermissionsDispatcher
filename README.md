# PermissionsDispathcer
![](https://img.shields.io/badge/platform-android-orange.svg)
![](https://img.shields.io/badge/language-java-yellow.svg)
![](https://jitpack.io/v/com.iwdael/permissionsdispatcher.svg)
![](https://img.shields.io/badge/build-passing-brightgreen.svg)
![](https://img.shields.io/badge/license-apache--2.0-green.svg)
![](https://img.shields.io/badge/api-19+-green.svg)

OnPermission用于授权的手机的操作系统是Android 6及以上,只需要简单地配置项目，就可以实现动态授权。
## 使用说明
### 代码示例
```Java
new OnPermission(this).grant(new Permission() {
            @Override
            public String[] permissions() {
                return new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS
                };
            }
            //当用户同意所有的权限申请后才会回调此方法
            @Override
            public void onGranted(String[] pemissions) {
            }
            //只要用户没有同意申请的权限的其中之一，此方法便会回调
            @Override
            public void onDenied(String[] pemissions) {
            }
        });
```

#### 快速引入项目 
```Java
	dependencies {
                ...
	        compile 'com.iwdael:permissionsdispatcher:![](https://jitpack.io/v/com.iwdael/permissionsdispatcher.svg)'
	}
```
<br><br>
## 感谢浏览
如果你有任何疑问，请加入QQ群，我将竭诚为你解答。欢迎Star和Fork本仓库，当然也欢迎你关注我。
<br>
![Image Text](https://github.com/hacknife/CarouselBanner/blob/master/qq_group.png)
