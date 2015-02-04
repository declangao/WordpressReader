# WordPress Reader
An demonstration app to showcase Google's [Volley](https://android.googlesource.com/platform/frameworks/volley) library, asynchronous HTTP request and WordPress Json API.

## About
I recently came across a project to turn a WordPress site into a news reader app. After a bit of research, I ended up finding several paid services to do exactly that. With little luck finding an adequate free one, I decided to write my own. And this is the direct result of that. 

The project features JSON array & JSON object request, AppCompat Material Design, WebView, custom ListView adaptor and multi-language support. GUI is dynamically created based on post categories JSON data at run-time. It then loads posts page by page to fill the ListView. Post content is parsed and loaded into a WebView with proper styling when needed.

Note: This is still a work in progress. More features are coming soon. 

## Requirements
This project requires Volley library, a WordPress site (tested on WordPress 4.x) and [JSON API plugin](https://wordpress.org/plugins/json-api/) to function properly. Remember to set your own WordPress URL in Config.java before trying it out.

I added a Floating Action Button to go for the full Material Design experience. Unfortunately, Floating Action Button is not a part of the SDK. It's the same thing with Snack Bar. Part of Material Design but not included in SDK. What's with that, Google? Instead of making the Floating Action Button myself, I used [this thrid-party library](http://github.com/futuresimple/android-floating-action-button) . So to setup the project, you will also need to add this dependancy to your `build.gradle` file:

```groovy
dependencies {
    compile 'com.getbase:floatingactionbutton:1.7.0'
}
```

PS: This project uses Disqus commenting system, which is better than WordPress's own commenting system in my opinion. So you will need to have Disqus installed on your WordPress site as well. It's quite easy to switch back to the original commenting system though.

## Screenshots
![Main GUI](http://i.imgur.com/NL1Jyqb.png)
![Article](http://i.imgur.com/eyPJ7A8.png)
![Comments](http://i.imgur.com/xP6lZqi.png)

## License
Copyright Declan Gao [@DeclanGao](http://twitter.com/DeclanGao/) © 2015.

Licensed under Apache 2.0 license.