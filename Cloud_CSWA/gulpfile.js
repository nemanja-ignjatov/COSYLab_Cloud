var fs = require('fs'),
    gulp = require('gulp'),
    usemin = require('gulp-usemin'),
    wrap = require('gulp-wrap'),
    connect = require('gulp-connect'),
    watch = require('gulp-watch'),
    minifyCss = require('gulp-cssnano'),
    minifyJs = require('gulp-uglify'),
    concat = require('gulp-concat'),
    less = require('gulp-less'),
    rename = require('gulp-rename'),
    minifyHTML = require('gulp-htmlmin'),
    gulpif = require('gulp-if'),
    gulpNgConfig = require('gulp-ng-config')
feAppConstants = require('./buildConstants.json');
    
var paths = {
    srcJs: 'src/*.js',
    pagesJs: 'src/pages/**/*.js',
    directivesJs: 'src/directives/**/*.js',
    servicesJs: 'src/services/**/*.js',
    pagesHtml: 'src/pages/**/*.html',
    directivesHtml: 'src/directives/**/*.html',
    styles: 'src/less/*.less',
    resources: 'src/resources/**/*.*',
    fonts: 'src/resources/fonts/**/*.*',
    index: 'src/index.html',
};

fs.unlink('./src/buildConstants.js', function (err) {
    console.log("buildConstants.js removed or not existed.");
});

var env = feAppConstants.FE_APP_CONFIGURATION.FE_APP_ENVIRONMENT || 'dev'; // Default environment if dev

console.log("Building for " + (env === 'prod' ? "PRODUCTION" : "DEVELOPMENT") + " environment");
/**
 * Handle bower components from index
 */
gulp.task('usemin', function () {
    return gulp.src(paths.index)
        .pipe(usemin({
            js: [minifyJs(), 'concat'],
            css: [minifyCss({keepSpecialComments: 0}), 'concat'],
        }))
        .pipe(gulp.dest('build/'));
});

/**
 * Handle custom files
 */
gulp.task('build-custom', ['custom-resources', 'custom-config', 'custom-src-js', 'custom-less', 'custom-html', 'custom-fonts']);

gulp.task('custom-resources', function () {
    return gulp.src(paths.resources)
        .pipe(gulp.dest('build/resources'));
});

gulp.task('custom-src-js', ['custom-config'], function () {
    return gulp.src([paths.srcJs, paths.servicesJs, paths.pagesJs, paths.directivesJs])
        .pipe(gulpif(env === 'prod', minifyJs())) // minify only in production environment
        .pipe(concat('app.min.js'))
        .pipe(gulp.dest('build/js'));
});

gulp.task('custom-config', function () {
    return gulp.src('buildConstants.json')
        .pipe(gulpNgConfig('cosyApp.buildConstants'))
        .pipe(gulp.dest('src/'));
});

gulp.task('custom-less', function () {
    return gulp.src(paths.styles)
        .pipe(less())
        .pipe(concat('app.css'))
        .pipe(gulp.dest('build/css'));
});

gulp.task('custom-html', function () {
    return gulp.src([paths.pagesHtml, paths.directivesHtml])
        .pipe(minifyHTML())
        .pipe(gulp.dest('build/html'));
});
gulp.task('custom-fonts', function () {
    return gulp.src([paths.fonts])
        .pipe(gulp.dest('build/lib/fonts'));
});
/**
 * Watch custom files
 */
gulp.task('watch', function () {
    gulp.watch([paths.resources], ['custom-resources']);
    gulp.watch([paths.styles], ['custom-less']);
    gulp.watch([paths.srcJs], ['custom-src-js']);
    gulp.watch([paths.pagesJs], ['custom-src-js']);
    gulp.watch([paths.directivesJs], ['custom-src-js']);
    gulp.watch([paths.servicesJs], ['custom-src-js']);
    gulp.watch([paths.pagesHtml], ['custom-html']);
    gulp.watch([paths.directivesHtml], ['custom-html']);
    gulp.watch([paths.index], ['usemin']);
});

/**
 * Live reload server
 */
gulp.task('webserver', function () {
    connect.server({
        root: 'build',
        livereload: true,
        port: 9999
    });
});

gulp.task('livereload', function () {
    gulp.src(['build/**/*.*'])
        .pipe(watch(['build/**/*.*']))
        .pipe(connect.reload());
});

/**
 * Gulp tasks
 */
gulp.task('build', ['usemin', 'build-custom']);
gulp.task('default', ['build', 'webserver', 'livereload', 'watch']);