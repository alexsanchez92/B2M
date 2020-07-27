2
module.exports = function(grunt) {
	var gtx = require('gruntfile-gtx').wrap(grunt);

    gtx.loadAuto();

    var gruntConfig = require('./grunt');
    gruntConfig.package = require('./package.json');

    gtx.config(gruntConfig);

    // We need our bower components in order to develop
    gtx.alias('build:dashv', ['compass:dashv', 'clean:dashv', 'copy:dashv', 'concat:dashv', 'cssmin:dashv', 'uglify:dashv']);
    gtx.alias('build:rtlversion', ['compass:rtlversion', 'clean:rtlversion', 'copy:rtlversion', 'concat:rtlversion', 'cssmin:rtlversion', 'uglify:rtlversion']);

    gtx.alias('release', ['bower-install-simple', 'build:dev', 'bump-commit']);


    gtx.finalise();
};
