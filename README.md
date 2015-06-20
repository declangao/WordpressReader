# WordPress Reader
A proof of concept app to showcase Google's [Volley](https://android.googlesource.com/platform/frameworks/volley) library, asynchronous HTTP request and WordPress JSON API.

## About
I recently came across a project to turn a WordPress site into a news reader app. After a bit of research, I ended up finding several paid services to do exactly that. With little luck finding an adequate free one, I decided to write my own. And this is the direct result of that. 

The project features JSON request, AppCompat Material Design, pull to refresh, WebView, custom ListView adaptor and multi-language support. GUI is dynamically created based on post categories JSON data at run-time. It then loads posts page by page to fill the ListView. Post content is parsed and loaded into a WebView with proper styling when needed.

PS: This is still a work in progress. More features are coming soon, probably... 

## Requirements
* Volley library (included)
* A WordPress site (tested on WordPress 4.x)
* [JSON API WordPress plugin](https://wordpress.org/plugins/json-api/) 

Remember to set your own WordPress URL in Config.java before trying it out.

I used [Disqus](https://disqus.com/) commenting system to remedy the  spamming issue. So you will need to have Disqus installed on your WordPress site as well. If you want to use WordPress's own comment function, you need to modify the code to read comments from API instead of rendering a Disqus webpage.

## Screenshots
![Main GUI](http://i.imgur.com/NL1Jyqb.png)
![Article](http://i.imgur.com/eyPJ7A8.png)
![Comments](http://i.imgur.com/xP6lZqi.png)

## License
Copyright [DeclanGao](http://twitter.com/DeclanGao/) © 2015.

Licensed under GPL v3 License.