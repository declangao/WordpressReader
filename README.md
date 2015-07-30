# WordPress Reader
A proof of concept app to demonstrate Google's [Volley](https://android.googlesource.com/platform/frameworks/volley) library, asynchronous HTTP request and WordPress JSON API.

## Features
* Asynchronous HTTP request
* AppCompat Material Design
* Collapsing Toolbar with CoordinatorLayout
* RecyclerView and CardView
* ViewPager
* Pull to refresh
* Nested fragments
* JSON parsing
* Android Wear notification (in case you want to read an article on your watch for god-knows-what reason)

## About
I recently came across a project to turn a WordPress site into a news reader app. After a bit of research, I ended up finding several paid services to do exactly that. With little luck finding an adequate free one, I decided to write my own. And this is the direct result of that. 

The entire GUI is dynamically created based on post categories JSON data at run-time. It then loads posts page by page to fill the ListView. Post content is parsed and loaded into a WebView with proper styling when needed.

PS: This is still a work in progress. More features are coming soon, probably... 

## Requirements
* Volley library
* A WordPress site (tested on WordPress 4.x)
* [JSON API WordPress plugin](https://wordpress.org/plugins/json-api/) 

Remember to set your own WordPress URL in Config.java before trying it out.

I used [Disqus](https://disqus.com/) commenting system to remedy the  spamming issue. So you will need to have Disqus installed on your WordPress site as well. If you want to use WordPress's own comment function, you need to modify the code to read comments from API instead of rendering a Disqus webpage.

## Todos
1. <del>Use RecyclerView and CardView</del>
2. <del>Add collapsing Toolbar<del>
3. Switch to OkHttp

## Known Issues
1. Multiple issues with Actionbar.
2. Youtube video playback seems to be broken.

Both issues appeared after adding collapsing Toolbar. I'll investigate further as soon as I have some time to work on this project.

## Screenshots
![Main GUI](http://i.imgur.com/h3IKdml.png)

*Main GUI*

![Article expanded](http://i.imgur.com/lqF4S4N.png)

*Article with expanded Toolbar*

![Article collapsed](http://i.imgur.com/J440MXz.png)

*Article with collapsed Toolbar*

![Comments](http://i.imgur.com/xP6lZqi.png)

*Comments*

![Android Wear](http://i.imgur.com/w3VrO87.png)

*Android Wear*

## License
Copyright [DeclanGao](http://twitter.com/DeclanGao/) © 2015.

Licensed under GPL v3 License.