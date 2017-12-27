# OnPermission  [![](https://jitpack.io/v/aliletter/onpermission.svg)](https://jitpack.io/#aliletter/onpermission)
OnPermission is used to  authorize mobile phone which operating system is android and above 6.0 .  You only need to simply configure the project and will be able to achieve dynamic authorization. (OnPermission also with instructions when you apply for permission .[中文文档](https://github.com/aliletter/OnPermisson/blob/master/README_CHINESE.md)
## Instruction
### Code Sample
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
            //The method will be callback to this method when the user agrees to all permission applications
            @Override
            public void onGranted(String[] pemissions) {
            }
            //This method will be callback as long as the user does not have one of the permissions to agree to the application.
            @Override
            public void onDenied(String[] pemissions) {
            }
        });
```
## How to
To get a Git project into your build:
### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories.   [click here for details](https://github.com/aliletter/CarouselBanner/blob/master/root_build.gradle.png)
```Java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Step 2. Add the dependency
Add it in your application module build.gradle at the end of dependencies where you want to use.   [click here for details](https://github.com/aliletter/CarouselBanner/blob/master/application_build.gradle.png)
```Java
	dependencies {
                ...
	        compile 'com.github.aliletter:onpermission:v1.0.8'
	}
```
 <br><br>
## Thank you for your browsing
If you have any questions, please join the QQ group. I will do my best to answer it for you. Welcome to star and fork this repository, alse follow me.
<br>
![Image Text](https://github.com/aliletter/CarouselBanner/blob/master/qq_group.png)
