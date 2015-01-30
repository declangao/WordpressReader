# WordPress Reader
---
An demonstration app to showcase Google's [Volley](https://android.googlesource.com/platform/frameworks/volley) library and WordPress Json API.


## About
I recently came across a project to turn a WordPress site into a news reader app. After a bit of research, I ended up finding several paid services to do exactly that. With little luck finding an adequate free one, I decided to write my own. And this is the direct result of that. 


The project features JSON array & JSON object request, AppCompat Material Design, WebView, custom ListView adaptor and multi-language support. It first reads post categories and dynamically create the GUI. Then it loads posts page by page to fill the ListView.


Note: This is still a work in progress. More features are coming soon. 


## Requirements
This project requires Volley library, a WordPress site (tested on WordPress 4.x) and [JSON API plugin](https://wordpress.org/plugins/json-api/) to function properly. Remember to set your own WordPress URL before trying it out.

PS: This project uses Disqus commenting system, which is better than WordPress's own commenting system in my opinion. So you will need to have Disqus installed on your WordPress site as well. It's quite easy to switch back to the original commenting system though.

## Screenshots
![Main GUI](http://i.imgur.com/QYXzoue.png)
![Article](http://i.imgur.com/lDPKvUB.png)
![Comments](http://i.imgur.com/r68Hvx2.png)


## Copyright and Licensing
Copyright Declan Gao [@DeclanGao](http://twitter.com/DeclanGao/) © 2015.
This app is distributed under Apache 2.0 License.