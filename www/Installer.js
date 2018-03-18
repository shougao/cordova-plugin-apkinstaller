var exec = require('cordova/exec');

exports.install = function (arg0, success, error) {
    exec(success, error, 'Installer', 'install', [arg0]);
};
