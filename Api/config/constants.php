<?php
/**
 * Provee las constantes para conectarse a la base de datos
 * Mysql.
 */
define("HOST", "localhost");// Nombre del host
define("DB", "oopp"); // Nombre de la base de controladores
define("USER", "root"); // Nombre del usuario
define("PASS", ""); // Contraseña

define("X_RATIO", 600); // Constraseña
define("Y_RATIO", 600); // Constraseña

define("URL_IMAGE_TEMP", "../images/temp/"); // Relative url for TEMP images
define("URL_IMAGE_FOUNDS", "../images/found/"); // Relative url for FOUND images
define("URL_IMAGE_LOSTS", "../images/lost/"); // Relative url for LOST images

define("ABS_URL_IMAGE_FOUNDS", "/b2m/images/found/"); // Relative url for FOUND images
define("ABS_URL_IMAGE_LOSTS", "/b2m/images/lost/"); // Relative url for LOST images

class Response {
	public $code;
	public $status;

    function __construct($code, $status){
    	$this->code = $code;
    	$this->status = $status;
    }
}