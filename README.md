# WordPress Reader
An demonstration app to showcase Google's [Volley](https://android.googlesource.com/platform/frameworks/volley) library, asynchronous HTTP request and WordPress Json API.

## About
I recently came across a project to turn a WordPress site into a news reader app. After a bit of research, I ended up finding several paid services to do exactly that. With little luck finding an adequate free one, I decided to write my own. And this is the direct result of that. 

The project features JSON array & JSON object request, AppCompat Material Design, WebView, custom ListView adaptor and multi-language support. GUI is dynamically created based on post categories JSON data at run-time. It then loads posts page by page to fill the ListView. Post content is parsed and loaded into a WebView with proper styling when needed.

Note: This is still a work in progress. More features are coming soon. 

## Requirements
This project requires Volley library, a WordPress site (tested on WordPress 4.x) and [JSON API plugin](https://wordpress.org/plugins/json-api/) to function properly. Remember to set your own WordPress URL before trying it out.

PS: This project uses Disqus commenting system, which is better than WordPress's own commenting system in my opinion. So you will need to have Disqus installed on your WordPress site as well. It's quite easy to switch back to the original commenting system though.

## Screenshots
![Main GUI](http://i.imgur.com/OsWtR5K.png)
![Article](http://i.imgur.com/eyPJ7A8.png)
![Comments](http://i.imgur.com/xP6lZqi.png)

## License
Copyright Declan Gao [@DeclanGao](http://twitter.com/DeclanGao/) © 2015.

Licensed under Apache 2.0 license.