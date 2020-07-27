<?php
    use Phalcon\Filter as Filter;

//obtenemos todos los Perdidos
$app->get('/found', function() use ($app) 
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

    $founds = Found::find(array($query, /*'columns'=> 'id, title, description, foundDate, haveIt, placeId, placeDetails, hasPhoto, createdAt',*/'order'=> 'createdAt desc', 'offset'=> $start, 'limit'=> $limit));
    $count = Found::count(array($query));    

    $response = new Response(200, "OK");

    $return['response'] = $response;
    $return['count'] = $count;
    //$return['data'] = $founds;
    $return['data'] = array();

    foreach($founds as $found){
        $res = AppFound::toArray($found);
        array_push($return['data'], $res);
    }
    return $return;
});

//buscamos Found por su texto
$app->get('/found/{search}', function($search) use ($app) 
{   
    $filter = new Filter();
    $start = 0; $limit = 20;

    //Obtener los parametros de la url si existen
    $parts = parse_url($app->request->getURI());
    if(isset($parts['query'])){
        parse_str($parts['query'], $queryParams);
        $limit = $filter->sanitize($queryParams['limit'], "int"); $start = $limit*($filter->sanitize($queryParams['page'], "int")-1);
    }

    $query = "title LIKE '%$search%' OR description LIKE '%$search%' OR placeDetails LIKE '%$search%' AND status=1";

    $founds = Found::find(array($query));
    $count = Found::count(array($query));

    $response = new Response(200, "OK");

    $return['response'] = $response;
    $return['count'] = $count;
    //$return['data'] = $founds;
    return $return;
});

//buscamos Found por Id
$app->get('/found/{id:[0-9]+}', function($id) use ($app) 
{
    $found = Found::findFirst(array( "id = $id AND status=1" ));

    if($found){
        $response = new Response(200, "OK");
        $res = AppFound::toArray($found);
        $return['data'] = $res;
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

//buscamos Similares al Encontrado
$app->get('/found/{id:[0-9]+}/similar', function($id) use ($app) 
{
    $filter = new Filter();
    $start = 0; $limit = 20;

    //Obtener los parametros de la url si existen
    $parts = parse_url($app->request->getURI());
    if(isset($parts['query'])){
        parse_str($parts['query'], $queryParams);
        $limit = $filter->sanitize($queryParams['limit'], "int"); $start = $limit*($filter->sanitize($queryParams['page'], "int")-1);
    }

    $found = Found::findFirst($id);

    if(!$found){        
        $response = new Response(404, "Not Found");
        $return['response'] = $response;
        return $return;
    }

    $return['data'] = array();

    $title = $found->getTitle();
    $foundDate = $found->getFoundDate();
    $haveIt = $found->getHaveIt();
    $userId = $found->users->getId();
    $userName = $found->users->getName();

    $query = "SELECT DISTINCT item.* FROM Lost item LEFT JOIN Users u ON u.id!=$userId AND u.name LIKE '%$userName%' WHERE item.status=1 AND item.id!=$id AND item.userId!=$userId AND item.title LIKE '%$title%' OR ('$foundDate' BETWEEN item.startDate AND item.endDate)";

    if($found->buildings){
        $placeId = $found->buildings->getId();
        $query .= " OR item.placeId=$placeId";
    }
    if($found->holder && !$haveIt){
        $placeId = $found->holder->getId();
        $query .= " OR item.placeId=$placeId";
    }

    $count = $app->modelsManager->executeQuery($query);
    $return['count'] = count($count);

    $query .= " ORDER BY item.createdAt desc LIMIT $start, $limit";

    //echo $query;

    $items = $app->modelsManager->executeQuery($query);

    foreach($items as $item){
        $res = AppLost::toArray($item);
        array_push($return['data'], $res);
    }

    $response = new Response(200, "OK");

    $return['response'] = $response;
    return $return;
});

//****************
//*** POST
//****************
$app->post('/found', function() use ($app) 
{
    $filter = new Filter();

    if(!isset($_POST['title']) || !isset($_POST['foundDate']) || !isset($_POST['haveIt'])){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $found = new Found();

    $haveIt = $filter->sanitize($_POST['haveIt'], "int");

    $found->setUserId($app->userId);//$app->userId;);
    $found->setTitle($filter->sanitize($_POST['title'], "string"));

    if(isset($_POST['description']))
        $found->setDescription($filter->sanitize($_POST['description'], "string"));

    $found->setFoundDate(DateTime::createFromFormat('d-m-Y', $filter->sanitize($_POST['foundDate'], "string"))->format('Y-m-d'));

    if(isset($_POST['property']))
        $found->setProperty($filter->sanitize($_POST['property'], "string"));

    if(isset($_POST['placeId']))
   		$found->setPlaceId($filter->sanitize($_POST['placeId'], "int"));

    if(isset($_POST['placeDetails']))
        $found->setPlaceDetails($filter->sanitize($_POST['placeDetails'], "string"));

    if($haveIt==0){
        $found->setHaveIt(0);
        if(isset($_POST['havePlaceId']))
            $found->setHavePlaceId($filter->sanitize($_POST['havePlaceId'], "int"));
        if(isset($_POST['havePlaceDetails']))
            $found->setHavePlaceDetails($filter->sanitize($_POST['havePlaceDetails'], "string"));
    }
    else{
        $found->setHaveIt(1);
    }

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
            rename(URL_IMAGE_TEMP . $tempName, URL_IMAGE_FOUNDS . $newName);
            
            $found->setImage($newName);
            $found->setHasPhoto(1);

            break;
        }
    }
    else{
        $found->setHasPhoto(0);
    }

    // Check if the insertion was successful
    if ($found->save()) {
        $response = new Response(201, "Created");
        //$returnId = $found->getId();//$found->getModel()->uniqueId;
        //$found = Found::findFirst($found->getId());
        $return['data'] = $found->getId();
    } else {
        // Create a response
        $response = new Response(500, "Internal server error");

        // Send errors to the client
        $errors = array();
        foreach ($found->getMessages() as $message) {
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
$app->put('/found/{id:[0-9]+}', function($id) use ($app) 
{
	$filter = new Filter();

    if(!$this->request->getPut('title') && !$this->request->getPut('description') && !$this->request->getPut('foundDate') && !$this->request->getPut('haveIt') && !$this->request->getPut('placeId') && !$this->request->getPut('placeDetails')){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $found = Found::findFirst(array("id=$id AND status=1"));

    if($found){
        if($found->getUserId()==$app->userId){

		    if($this->request->getPut('title')!=null)
		    	$found->setTitle($filter->sanitize($this->request->getPut('title'), "string"));

		    if($this->request->getPut('description')!=null)
		    	$found->setDescription($filter->sanitize($this->request->getPut('description'), "string"));

            if($this->request->getPut('property')!=null)
                $found->setProperty($filter->sanitize($this->request->getPut('property'), "string"));

            if($this->request->getPut('foundDate')!=null)
                $found->setFoundDate($filter->sanitize($this->request->getPut('foundDate'), "string"));

		    if($this->request->getPut('haveIt')!=null)
		    	$found->setHaveIt($filter->sanitize($this->request->getPut('haveIt'), "int"));

		    if($this->request->getPut('placeId')!=null)
		    	$found->setPlaceId($filter->sanitize($this->request->getPut('placeId'), "int"));

		    if($this->request->getPut('placeDetails')!=null)
		    	$found->setPlaceDetails($filter->sanitize($this->request->getPut('placeDetails'), "string"));


            if($found->save()){
                $response = new Response(200 , "OK");
        		$return['data'] = $found;
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

$app->put('/found/{id:[0-9]+}/recover', function($id) use ($app) 
{
    $found = Found::findFirst(array("id=$id AND status!=4"));

    if($found){
        if($found->getUserId()==$app->userId){
        	$found->setStatus(2);
            if($found->save()){
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

$app->put('/found/{id:[0-9]+}/place', function($id) use ($app) 
{
	$filter = new Filter();
    $found = Found::findFirst(array("id=$id AND status=1 AND haveIt=1"));

    if($found){
        if($found->getUserId()==$app->userId){

            if(!$this->request->getPut('placeId')){
                $response = new Response(400, "Bad Request");
                $return['response'] = $response;
                return $return;
            }

            try{
            	$found->setHaveIt(0);
            	$found->setHavePlaceId($filter->sanitize($this->request->getPut('placeId'), "int"));
            	$found->setHavePlaceDetails($placeDetails = $filter->sanitize($this->request->getPut('placeDetails'), "string"));

                if($found->save()){
                    $response = new Response(200 , "OK");
                }
                else{
                    $response = new Response(500, "Internal server error");            
                }
            }catch(PDOException $e){
                $response = new Response(400, "Bad Request");
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

//****************
//*** DELETE
//****************
$app->delete('/found/{id:[0-9]+}', function($id) use ($app) 
{
    $userId = $app->userId;
    $found = Found::findFirst($id);

    if($found){
        if($found->getUserId()==$userId){
            if($found->delete()){
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