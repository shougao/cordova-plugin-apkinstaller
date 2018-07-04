#!/usr/bin/env node

module.exports = function(context) {

    var APPLICATION_CLASS = "com.example.template.MyApplication";

    var fs = context.requireCordovaModule('fs'),
        path = context.requireCordovaModule('path');

    var platformRoot = path.join(context.opts.projectRoot, 'platforms/android');
    var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');

    if (fs.existsSync(manifestFile)) {
        fs.readFile(manifestFile, 'utf8', function (err, data) {
            if (err) {
                throw new Error('Unable to find AndroidManifest.xml: ' + err);
            }

            if (data.indexOf(APPLICATION_CLASS) == -1) {
                var result = data.replace(/<application/g, '<application android:name="' + APPLICATION_CLASS + '"');
                fs.writeFile(manifestFile, result, 'utf8', function (err) {
                    if (err) throw new Error('zqc Unable to write into AndroidManifest.xml: ' + err);
                })
            }
        });
    }
};