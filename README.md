# OnPermission
[![](https://img.shields.io/badge/platform-android-orange.svg)](https://github.com/hacknife) [![](https://img.shields.io/badge/language-java-yellow.svg)](https://github.com/hacknife) [![](https://jitpack.io/v/com.hacknife/onpermission.svg)](https://jitpack.io/#com.hacknife/onpermission) [![](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/hacknife)<br/><br/>
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
## 如何配置
将本仓库引入你的项目:
### Step 1. 添加JitPack仓库到Build文件
合并以下代码到项目根目录下的build.gradle文件的repositories尾。[点击查看详情](https://github.com/hacknife/CarouselBanner/blob/master/root_build.gradle.png)
```Java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Step 2. 添加依赖   
合并以下代码到需要使用的application Module的dependencies尾。[点击查看详情](https://github.com/hacknife/CarouselBanner/blob/master/application_build.gradle.png)
```Java
	dependencies {
                ...
	        compile 'com.hacknife:onpermission:tag'
	}
```
<br><br>
## 感谢浏览
如果你有任何疑问，请加入QQ群，我将竭诚为你解答。欢迎Star和Fork本仓库，当然也欢迎你关注我。
<br>
![Image Text](https://github.com/hacknife/CarouselBanner/blob/master/qq_group.png)
