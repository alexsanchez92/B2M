<?php

use Phalcon\Db\Adapter\Pdo\Mysql as MysqlPdo;
use Phalcon\Config\Adapter\Ini as ConfigIni;
use Phalcon\Logger;
use Phalcon\Events\Manager as EventsManager;
use Phalcon\Logger\Adapter\File as FileLogger;
use Phalcon\Db\Adapter\Pdo\Mysql as Connection;

$config = new ConfigIni('config/config.ini');

//registramos el directorio models
$loader = new \Phalcon\Loader();
$loader->registerDirs(    
	array(
        $config->application->modelsDir,
        $config->application->routesDir,
        $config->application->libraryDir
    )
)->register();

include('config/constants.php');

//creamos la instancia de la factoria
$di = new \Phalcon\DI\FactoryDefault();

//creamos la conexión con la base de datos
$di->set('db', function() use ($config)
{
    $eventsManager = new EventsManager();
    $logger = new FileLogger("debug.log");
    // Listen all the database events
    $eventsManager->attach('db', function ($event, $connection) use ($logger) {
        if ($event->getType() == 'beforeQuery') {
            $logger->log($connection->getSQLStatement(), Logger::INFO);
        }
    });

    $connection = new Connection(
        array(
            "host"     => $config->database->host,
            "username" => $config->database->username,
            "password" => $config->database->password,
            "dbname"   => $config->database->dbname,
            "charset"   =>"utf8"   
        )
    );

    // Assign the eventsManager to the db adapter instance
    $connection->setEventsManager($eventsManager);

    return $connection;
});

//creamos una micro aplicación, esto es útil para crear aplicaciones
$app = new \Phalcon\Mvc\Micro($di);

$userId;

$app->before(function() use ($app) {
    $ignore_url = array('register', 'login', 'signup', 'login/mobile');
    $request_url = str_replace('/b2m/api/','',$app->request->getURI());

    //echo "URL: ".$request_url."\n";

    $headers = apache_request_headers();

    if(!$app->request->isGet() && !in_array($request_url, $ignore_url)){

        if (isset($headers["api-key"])) {

            $token = $headers["api-key"];

            $user = Users::findFirst(array("token='$token' OR tokenMobile='$token'"));

            if($user){
                //echo "USER: ".$user->getName()."\n";
                //echo "KEY: ".$token."\n";
                $app->userId = $user->getId();
            }
            else{
                $response = new Response(401, "Unauthorized");      
                $app->response->setStatusCode($response->code, $response->status)->sendHeaders();
                return false;
            }
            return true;

            /*if (usuarios::validarClaveApi($claveApi)) {
                return usuarios::obtenerIdUsuario($claveApi);
            } else {
                throw new ExcepcionApi(
                    self::ESTADO_CLAVE_NO_AUTORIZADA, "Clave de API no autorizada", 401);
            }*/
        
        }
        else{
        	$response = new Response(401, "Unauthorized");    	
            $app->response->setStatusCode($response->code, $response->status)->sendHeaders();
        	return false;
        }
    }

    /*if(!in_array($request_url,$ignore_url)){
        $api_hash = $app->request->getHeader("API-HASH");
        $api_hash_type = substr($api_hash,0,1);
        $api_time = $app->request->getHeader("API-DATE");
        $content = $app->request->getRawBody();
        $hash = hash_hmac('sha256', $content, PRIVATE_KEY);

        $stoken_check = SecretTokens::count(array("secret_token='".$api_hash."' AND expiry_date > NOW()"));
        if($stoken_check > 0){
            if($api_hash == $hash && (strtotime($api_time) > strtotime("-5 minutes") && $api_time < date('Y-m-d H:i:s'))){
                return true;
            }
        }

        echo json_encode(array('ts' => time(), 'status' => false, 'data' => 'invalid call. Hash mismatch '.$hash));
        //, 'add' => $app->request->getHeaders()
        return false;
    }
    else {*/
        //return true;
    //}

});

$app->after(function() use ($app) {

    $response = '';
    $return = $app->getReturnedValue();
    $app->response->setHeader("Content-Type", "application/json");

    if(!array_key_exists('error_msg', $return)){
        $app->response->setStatusCode($return["response"]->code, $return["response"]->status)->sendHeaders();
        $data = array('ts' => time(), 'success' => 1, 'error' => 0);
    }
    else {
        $app->response->setStatusCode(500, "Internal server error")->sendHeaders();
        $data = array('ts' => time(), 'success' => 0, 'error' => 1);
    }
    
    if(array_key_exists('count', $return))
        $data = array_merge($data, array('count' => $return['count']));

    if(array_key_exists('code', $return))
        $data = array_merge($data, array('code' => $return['code']));

    if(array_key_exists('data', $return))
        $data = array_merge($data, array('data' => $return['data']));

    $response = json_encode($data);

    echo $response;
    //echo "RESP: ".$response."\n";
});

$app->notFound(function () use ($app) {
    $app->response->setStatusCode(404, "Not Found")->sendHeaders();
    echo json_encode(array('ts' => time(), 'success' => 0, 'data' => 'Not found'));
});

include('routes/lost.php');
include('routes/found.php');
include('routes/users.php');
include('routes/buildings.php');
include('routes/countries.php');
 	
$app->handle();

?>