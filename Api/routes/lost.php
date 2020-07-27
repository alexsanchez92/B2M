<?php
    use Phalcon\Filter as Filter;

//obtenemos todos los Perdidos
$app->get('/lost', function() use ($app) 
{   
    $filter = new Filter();

    $start = 0; $limit = 12;

    //Obtener los parametros de la url si existen
    $parts = parse_url($app->request->getURI());
    if(isset($parts['query'])){
        parse_str($parts['query'], $queryParams);
        $limit = $filter->sanitize($queryParams['limit'], "int"); $start = $limit*($filter->sanitize($queryParams['page'], "int")-1);
    }

    $query = "status=1";

    $losts = Lost::find(array($query,/* 'columns'=> 'id, title, description, startDate, endDate, placeId, placeDetails, hasPhoto, createdAt', */'order'=> 'createdAt desc', 'offset'=> $start, 'limit'=> $limit));
    $count = Lost::count(array($query));

    $response = new Response(200, "OK");

    $return['response'] = $response;
    $return['count'] = $count;
    //$return['data'] = $losts;
    $return['data'] = array();

    foreach($losts as $lost){
        $res = AppLost::toArray($lost);
        array_push($return['data'], $res);
    }
    return $return;
});

//buscamos Perdido por su id
$app->get('/lost/{id:[0-9]+}', function($id) use ($app) 
{
    $lost = Lost::findFirst(array( "id = $id AND (status=1 OR status=2)" ));    

    if($lost){
        $response = new Response(200, "OK");        
        $res = AppLost::toArray($lost);
        $return['data'] = $res;
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

//buscamos Similares al Perdido
$app->get('/lost/{id:[0-9]+}/similar', function($id) use ($app) 
{
    $filter = new Filter();
    $start = 0; $limit = 20;

    //Obtener los parametros de la url si existen
    $parts = parse_url($app->request->getURI());
    if(isset($parts['query'])){
        parse_str($parts['query'], $queryParams);
        $limit = $filter->sanitize($queryParams['limit'], "int"); $start = $limit*($filter->sanitize($queryParams['page'], "int")-1);
    }

    $lost = Lost::findFirst($id);

    if(!$lost){        
        $response = new Response(404, "Not Found");
        $return['response'] = $response;
        return $return;
    }

    $return['data'] = array();

    $title = $lost->getTitle();
    $startDate = $lost->getStartDate();
    $endDate = $lost->getEndDate();
    $userId = $lost->users->getId();
    $userName = $lost->users->getName();

    $query = "SELECT DISTINCT item.* FROM Found item LEFT JOIN Users u ON u.id!=$userId AND u.name LIKE '%$userName%' WHERE item.status=1 AND item.id!=$id AND item.userId!=$userId AND item.title LIKE '%$title%' OR (item.foundDate BETWEEN '$startDate' AND '$endDate')";

    if($lost->buildings){
        $placeId = $lost->buildings->getId();
        $query .= " OR item.placeId=$placeId OR (item.haveIt=0 AND item.havePlaceId IS NOT NULL AND item.havePlaceId=$placeId)";
    }

    $count = $app->modelsManager->executeQuery($query);
    $return['count'] = count($count);

    $query .= " ORDER BY item.createdAt desc LIMIT $start, $limit";

    //echo $query;

    $items = $app->modelsManager->executeQuery($query);

    foreach($items as $item){
        $res = AppFound::toArray($item);
        array_push($return['data'], $res);
    }

    $response = new Response(200, "OK");

    $return['response'] = $response;
    return $return;
});

//****************
//*** POST
//****************
$app->post('/lost', function() use ($app) 
{
    $filter = new Filter();

    if(!isset($_POST['title']) || !isset($_POST['startDate']) || !isset($_POST['endDate']) || !isset($_POST['placeId'])){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $lost = new Lost();

    $lost->setUserId($app->userId);//$app->userId;);
    $lost->setTitle($filter->sanitize($_POST['title'], "string"));

    if(isset($_POST['description']))
        $lost->setDescription($filter->sanitize($_POST['description'], "string"));

    $lost->setStartDate(DateTime::createFromFormat('d-m-Y', $filter->sanitize($_POST['startDate'], "string"))->format('Y-m-d'));    
    $lost->setEndDate(DateTime::createFromFormat('d-m-Y', $filter->sanitize($_POST['endDate'], "string"))->format('Y-m-d'));

    $lost->setPlaceId($filter->sanitize($_POST['placeId'], "string"));

    if(isset($_POST['placeDetails']))
        $lost->setPlaceDetails($filter->sanitize($_POST['placeDetails'], "string"));

    if($this->request->hasFiles()){

        foreach ($this->request->getUploadedFiles() as $file){

            if($file->getRealType()!='image/jpeg' && $file->getRealType()!='image/png'){
                $response = new Response(400, "Bad Request");
                $return['response'] = $response;
                return $return;
            }

            $tempName = $app->userId.time().$file->getName().'.'.$file->getExtension();
            $realType = $file->getRealType();
            $extension = $file->getExtension();
            $file->moveTo(URL_IMAGE_TEMP . $tempName);
            $imgUrl = URL_IMAGE_TEMP . $tempName;

            switch ($realType) {
                case 'image/jpeg':
                    $image = imagecreatefromjpeg($imgUrl);
                break;

                case 'image/png':
                    $image = imagecreatefrompng($imgUrl);
                break;
            }

            $max_ancho = X_RATIO; $max_alto = Y_RATIO;

            list($ancho, $alto) = getimagesize($imgUrl);

            $x_ratio = $max_ancho / $ancho;
            $y_ratio = $max_alto / $alto;

            if( ($ancho <= $max_ancho) && ($alto <= $max_alto) ){
               $ancho_final = $ancho;
               $alto_final = $alto;
            }
            else if (($x_ratio * $alto) < $max_alto){
               $alto_final = ceil($x_ratio * $alto);
               $ancho_final = $max_ancho;
            }
            else{
               $ancho_final = ceil($y_ratio * $ancho);
               $alto_final = $max_alto;
            }

            $tempImage = imagecreatetruecolor($ancho_final, $alto_final);
            imagecopyresampled($tempImage, $image, 0, 0, 0, 0, $ancho_final, $alto_final, $ancho, $alto);

            switch ($realType) {
                case 'image/jpeg':
                    imagejpeg($tempImage, $imgUrl, 100);
                break;
                
                case 'image/png':
                    imagesavealpha($tempImage, true);
                    $alpha = imagecolorallocatealpha($tempImage, 0, 0, 0, 127);
                    imagefill($tempImage, 0, 0, $alpha);
                    imagepng($tempImage, $imgUrl);
                break;
            }

            $md5file = md5_file(URL_IMAGE_TEMP . $tempName);
            $newName = md5(time().$app->userId.$md5file).'.'.$extension;            
            rename(URL_IMAGE_TEMP . $tempName, URL_IMAGE_LOSTS . $newName);
            
            $lost->setImage($newName);
            $lost->setHasPhoto(1);

            break;
        }
    }
    else{
        $lost->setHasPhoto(0);
    }

    // Check if the insertion was successful
    if ($lost->save()) {
        $response = new Response(201, "Created");
        //$returnId = $lost->getId();//$lost->getModel()->uniqueId;
        //$lost = Lost::findFirst($lost->getId());
        $return['data'] = $lost->getId();
    } else {
        // Create a response
        $response = new Response(500, "Internal server error");

        // Send errors to the client
        $errors = array();
        foreach ($lost->getMessages() as $message) {
            $errors[] = $message->getMessage();
        }
        $return['data'] = $errors;
    }

    $return['response'] = $response;
    return $return;
});

//****************
//*** PUT
//****************
$app->put('/lost/{id:[0-9]+}', function($id) use ($app) 
{
    $filter = new Filter();

    if(!$this->request->getPut('title') && !$this->request->getPut('description') && !$this->request->getPut('startDate') && !$this->request->getPut('endDate') && !$this->request->getPut('placeId') && !$this->request->getPut('placeDetails')){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $lost = Lost::findFirst(array("id=$id AND status=1"));

    if($lost){
        if($lost->getUserId()==$app->userId){

            if(!$this->request->getPut('title'))
                $lost->setTitle($filter->sanitize($this->request->getPut('title'), "string"));

            if(!$this->request->getPut('description'))
                $lost->setDescription($filter->sanitize($this->request->getPut('description'), "string"));

            if(!$this->request->getPut('startDate'))
                $lost->setStartDate($filter->sanitize($this->request->getPut('startDate'), "string"));

            if(!$this->request->getPut('endDate'))
                $lost->setEndDate($filter->sanitize($this->request->getPut('endDate'), "string"));

            if(!$this->request->getPut('placeId'))
                $lost->setPlaceId($filter->sanitize($this->request->getPut('placeId'), "int"));

            if(!$this->request->getPut('placeDetails'))
                $lost->setPlaceDetails($filter->sanitize($this->request->getPut('placeDetails'), "string"));

            if($lost->save()){
                $response = new Response(200 , "OK");
                $return['data'] = $lost;
            }
            else{
                $response = new Response(500, "Internal server error");            
            }
        }
        else{
            $response = new Response(403, "Forbidden");
        }
    }
    else
        $response = new Response(404, "Not Found");

    $return['response'] = $response;
    return $return;
});

$app->put('/lost/{id:[0-9]+}/recover', function($id) use ($app) 
{
    $lost = Lost::findFirst(array("id=$id AND status!=4"));

    if($lost){
        if($lost->getUserId()==$app->userId){
            $lost->setStatus(2);
            if($lost->save()){
                $response = new Response(200 , "OK");
            }
            else{
                $response = new Response(500, "Internal server error");            
            }
        }
        else{
            $response = new Response(403, "Forbidden");
        }
    }
    else
        $response = new Response(404, "Not Found");

    $return['response'] = $response;
    return $return;
});


/*
$app->put('/records/{id:[0-9]+}/visibility', function($id) use ($app) 
{
    $filter = new Filter();

    $userId = $app->userId;
    $user = Users::findFirst($userId);
    $record = Records::findFirst($id);

    if($record){
        if($record->userId==$userId || $user->userType=='master'){
            if($record->recordVisibility<2){
                ($record->recordVisibility)?$record->recordVisibility=0:$record->recordVisibility=1;
                if($record->save()){
                    $response = new Response(200 , "OK");
                    $return['data'] = $record->recordVisibility;
                }
                else{
                    $response = new Response(500, "Internal server error");            
                }
            }
            else{
                $response = new Response(405, "Method Not Allowed");
            }
        }
        else{
            $response = new Response(403, "Forbidden");
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});
*/

//****************
//*** DELETE
//****************
$app->delete('/lost/{id:[0-9]+}', function($id) use ($app) 
{
    $userId = $app->userId;
    $lost = Lost::findFirst($id);

    if($lost){
        if($lost->getUserId()==$userId){
            
            if($lost->delete()){
                //$response = new Response(204 , "No Content");
                $response = new Response(200 , "OK");
            }
            else{
                $response = new Response(500, "Internal server error");            
            }
        }
        else{
            $response = new Response(403, "Forbidden");
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

?>