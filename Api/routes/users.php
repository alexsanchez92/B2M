<?php
    use Phalcon\Filter as Filter;

//Get Search
$app->get('/search', function() use ($app) 
{
    $filter = new Filter();
    $start = 0; $limit = 20;

    if(!isset($_GET['type'])/* && (!isset($_GET['startDate']) || !isset($_GET['endDate']))*/){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        $return['data'] = '';
        return $return;
    }

    if(isset($_GET['limit']) && isset($_GET['page'])){
        $limit = $filter->sanitize($_GET['limit'], "int"); $start = $limit*($filter->sanitize($_GET['page'], "int")-1);
    }

    $return['data'] = array();

    $type = $_GET['type'];
    $search = $filter->sanitize($_GET['search'], "string");

    switch ($type) {
        case 0:
            $query = "SELECT DISTINCT item.* FROM Found item";
        break;

        case 1:
            $query = "SELECT DISTINCT item.* FROM Lost item";
        break;
    }

    if(isset($_GET['place'])){
        $place = $filter->sanitize($_GET['place'], "string");
        $query .= " RIGHT JOIN Buildings b ON item.placeId=b.id AND b.name LIKE '%$place%'";
    }

    $query .= " WHERE item.status=1 AND (item.title LIKE '%$search%' OR item.description LIKE '%$search%')";

    if(isset($_GET['startDate']) && isset($_GET['endDate'])){
        $startDate = $filter->sanitize($_GET['startDate'], "string");
        $endDate = $filter->sanitize($_GET['endDate'], "string");

        ($type==0)?$query .= " AND (item.foundDate BETWEEN '$startDate' AND '$endDate')":$query .= " AND (item.startDate BETWEEN '$startDate' AND '$endDate' OR item.endDate BETWEEN '$startDate' AND '$endDate' OR item.startDate>='$startDate' AND item.endDate<='$endDate')";
    }

    /*
    if(isset($_GET['property'])){
        $startDate = $filter->sanitize($_GET['property'], "int");

        $query .= " AND item.property LIKE '%al%'";
    } */
    
    $query .= " ORDER BY createdAt desc";
    $queryLimit = $query." LIMIT $start, $limit";

    //echo $query;

    $items = $app->modelsManager->executeQuery($queryLimit);

    foreach($items as $item){
        ($type==0)?$res = AppFound::toArray($item):$res = AppLost::toArray($item);
        array_push($return['data'], $res);
    }

    $count = $app->modelsManager->executeQuery($query);
    $count = count($count);


    /*switch ($type) {
        case 0:
            $founds = Found::find(array($query, 'order'=> 'createdAt desc', 'offset'=> $start, 'limit'=> $limit));
            $count = Found::count(array($query));

            foreach($items as $found){
                $res = AppFound::toArray($found, $app);
                array_push($return['data'], $res);
            }
            break;

        case 1:
            $losts = Lost::find(array($query, 'order'=> 'createdAt desc', 'offset'=> $start, 'limit'=> $limit));
            $count = Lost::count(array($query));

            foreach($losts as $lost){
                $res = AppLost::toArray($lost, $app);
                array_push($return['data'], $res);
            }

            break;
    }*/

    $response = new Response(200, "OK");
    $return['response'] = $response;
    $return['count'] = $count;

    return $return;
});

//Get Users
$app->get('/users', function() use ($app) 
{
    $filter = new Filter();
    $start = 0; $limit = 20;

    if(isset($_GET['limit']) && isset($_GET['page'])){
        $limit = $filter->sanitize($_GET['limit'], "int"); $start = $limit*($filter->sanitize($_GET['page'], "int")-1);
    }

    $query = "status=1";
    $users = Users::find(array($query,/* 'columns'=> 'id, email, phone, name', 'order'=> 'createdAt desc', */'offset'=> $start, 'limit'=> $limit));
    $count = Users::count(array($query));

    $response = new Response(200, "OK");
    $return['response'] = $response;
    $return['count'] = $count;
    $return['data'] = array();

    foreach($users as $user){
        $res = AppUser::toArrayMin($user);
        array_push($return['data'], $res);
    }

    return $return;
});

//Get user by ID
$app->get('/users/{id:[0-9]+}', function($id) use ($app) 
{
    $user = Users::findFirst($id);

    if($user){
        if($user->getStatus()=='deleted'){
            $response = new Response(410, "GONE");
            $return['response'] = $response;
            return $return;
        }
        $response = new Response(200, "OK");
        $return['data'] = array('users'=>$user);
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

//Get User Losts
$app->get('/users/{id:[0-9]+}/lost', function($id) use ($app) 
{    
    $filter = new Filter();
    $start = 0; $limit = 20;

    if(isset($_GET['limit']) && isset($_GET['page'])){
        $limit = $filter->sanitize($_GET['limit'], "int"); $start = $limit*($filter->sanitize($_GET['page'], "int")-1);
    }

    $user = Users::findFirst($id);

    if($user){
        if($user->getStatus()=='deleted'){
            $response = new Response(410, "GONE");
            $return['response'] = $response;
            return $return;
        }
        $query = "userId = $id AND status=1";
        $losts = $user->getLost(array($query,/* 'columns'=> 'id, title, description, image, startDate, endDate, placeId, placeDetails, createdAt',*/ 'order'=> 'updatedAt desc', 'offset'=> $start, 'limit'=> $limit));
        $count = Lost::count(array($query));
        $response = new Response(200, "OK");
        //$return['data'] = $losts;
        $return['data'] = array();
        $return['count'] = $count;

        foreach($losts as $lost){
            $res = AppLost::toArrayWithUser($lost, $user);
            array_push($return['data'], $res);
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

//Get User Losts
$app->get('/users/{id:[0-9]+}/recover', function($id) use ($app) 
{    
    $filter = new Filter();
    $start = 0; $limit = 20;

    if(isset($_GET['limit']) && isset($_GET['page'])){
        $limit = $filter->sanitize($_GET['limit'], "int"); $start = $limit*($filter->sanitize($_GET['page'], "int")-1);
    }

    $user = Users::findFirst($id);

    if($user){
        if($user->getStatus()=='deleted'){
            $response = new Response(410, "GONE");
            $return['response'] = $response;
            return $return;
        }
        $query = "userId = $id AND status=2";
        $losts = $user->getLost(array($query,/* 'columns'=> 'id, title, description, image, startDate, endDate, placeId, placeDetails, createdAt',*/ 'order'=> 'createdAt desc', 'offset'=> $start, 'limit'=> $limit));
        $count = Lost::count(array($query));
        $response = new Response(200, "OK");
        //$return['data'] = $losts;
        $return['data'] = array();
        $return['count'] = $count;

        foreach($losts as $lost){
            $res = AppLost::toArrayWithUser($lost, $user);
            array_push($return['data'], $res);
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

//Get User Found
$app->get('/users/{id:[0-9]+}/found', function($id) use ($app) 
{    
    $filter = new Filter();
    $start = 0; $limit = 20;

    if(isset($_GET['limit']) && isset($_GET['page'])){
        $limit = $filter->sanitize($_GET['limit'], "int"); $start = $limit*($filter->sanitize($_GET['page'], "int")-1);
    }

    $user = Users::findFirst($id);

    if($user){
        if($user->getStatus()=='deleted'){
            $response = new Response(410, "GONE");
            $return['response'] = $response;
            return $return;
        }
        $query = "userId = $id AND haveIt=1 AND (status=1 OR status=2)";
        $founds = $user->getFound(array($query,/* 'columns'=> 'id, title, description, image, foundDate, property, haveIt, placeId, placeDetails, createdAt',*/ 'order'=> 'createdAt desc', 'offset'=> $start, 'limit'=> $limit));
        $count = Found::count(array($query));
        $response = new Response(200, "OK");
        //$return['data'] = $founds;
        $return['data'] = array();
        $return['count'] = $count;

        foreach($founds as $found){
            $res = AppFound::toArrayWithUser($found, $user);
            array_push($return['data'], $res);
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

//****************
//*** POST
//****************

//Login
$app->post('/login', function() use ($app) 
{
    $filter = new Filter();

    if(!isset($_POST['email']) || !isset($_POST['password'])){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        $return['data'] = '';
        return $return;
    }

    $email = $filter->sanitize($_POST['email'], "email");
    $password = $_POST['password'];

    $query = "email='$email' AND status=1";
    //$query = "email='$email' AND password='$password' AND status=1";

    $user = Users::findFirst(array($query));

    if($user){
        $password = hash('sha256', $password.$user->getSalt());

        if($user->getPassword()!=$password){
            $response = new Response(404, "Not Found");
            $return['response'] = $response;
            $return['data'] = '';
            return $return;
        }

        $date = new DateTime();
        $user->setToken(hash('sha256', $user->getId().$user->getEmail().$date->getTimestamp()));
        if($user->save()){

            $res = AppUser::toArray($user);
            $response = new Response(200, "OK");
            $return['data'] = $res;
        }
        else{            
            $response = new Response(500, "Internal server error");
            // Send errors to the client
            $errors = array();
            foreach ($user->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
            $return['data'] = $errors;
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }
    $return['response'] = $response;
    return $return;
});

//Login
$app->post('/login/mobile', function() use ($app) 
{
    $filter = new Filter();

    if(!isset($_POST['email']) || !isset($_POST['password'])){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        $return['data'] = '';
        return $return;
    }

    $email = $filter->sanitize($_POST['email'], "email");
    $password = $_POST['password'];

    $query = "email='$email' AND status=1";
    //$query = "email='$email' AND password='$password' AND status=1";

    $user = Users::findFirst(array($query));

    if($user){
        $password = hash('sha256', $password.$user->getSalt());

        if($user->getPassword()!=$password){
            $response = new Response(404, "Not Found");
            $return['response'] = $response;
            $return['data'] = '';
            return $return;
        }

        $date = new DateTime();
        $user->setTokenMobile(hash('sha256', "mobile".$user->getId().$user->getEmail().$date->getTimestamp()));
        if($user->save()){

            $res = AppUser::toArrayMobile($user);
            $response = new Response(200, "OK");
            $return['data'] = $res;
        }
        else{            
            $response = new Response(500, "Internal server error");
            // Send errors to the client
            $errors = array();
            foreach ($user->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
            $return['data'] = $errors;
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }
    $return['response'] = $response;
    return $return;
});

//LogOut
$app->post('/logout', function() use ($app) 
{
    $filter = new Filter();

    $user = Users::findFirst($app->userId);

    if($user){
        $user->setToken(null);
        if($user->save()){
            //$res = AppUser::toArray($user);
            $response = new Response(200, "OK");
            //$return['data'] = $res;
        }
        else{            
            $response = new Response(500, "Internal server error");
            // Send errors to the client
            $errors = array();
            foreach ($user->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
            $return['data'] = $errors;
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }
    $return['response'] = $response;
    return $return;
});

//LogOut
$app->post('/logout/mobile', function() use ($app) 
{
    $filter = new Filter();

    $user = Users::findFirst($app->userId);

    if($user){
        $user->setTokenMobile(null);
        if($user->save()){
            //$res = AppUser::toArray($user);
            $response = new Response(200, "OK");
            //$return['data'] = $res;
        }
        else{            
            $response = new Response(500, "Internal server error");
            // Send errors to the client
            $errors = array();
            foreach ($user->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
            $return['data'] = $errors;
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }
    $return['response'] = $response;
    return $return;
});

//LogOut
$app->post('/logout/mobile', function() use ($app) 
{
    $filter = new Filter();

    $user = Users::findFirst($app->userId);

    if($user){
        $user->setTokenMobile(null);
        if($user->save()){
            //$res = AppUser::toArray($user);
            $response = new Response(200, "OK");
            //$return['data'] = $res;
        }
        else{            
            $response = new Response(500, "Internal server error");
            // Send errors to the client
            $errors = array();
            foreach ($user->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
            $return['data'] = $errors;
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }
    $return['response'] = $response;
    return $return;
});

//Create User
$app->post('/signup', function() use ($app) 
{
    $filter = new Filter();

    if(!isset($_POST['email']) || !isset($_POST['phone']) || !isset($_POST['password'])){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        $return['data'] = '';
        return $return;
    }    

    $email = $filter->sanitize($_POST['email'], "email");
    $phone = $filter->sanitize($_POST['phone'], "int");

    $findEmail = Users::findFirst(array("email='$email'"));
    if($findEmail){
        $response = new Response(409, "Conflict");
        $return['response'] = $response;
        $return['code'] = 1;
        return $return;
    }

    $user = new Users();
    $user->setEmail($email);
    $user->setPhone($phone);

    if(isset($_POST['prefix'])){
        $prefix = $filter->sanitize($_POST['prefix'], "int");

        $findPhone = Users::findFirst(array("phone='$phone' AND prefixId=$prefix"));
        if($findPhone){
            $response = new Response(409, "Conflict");
            $return['response'] = $response;
            $return['code'] = 2;
            return $return;
        }
        $user->setPrefix($prefix);
    }
    else{
        $findPhone = Users::findFirst(array("phone='$phone' AND prefixId is NULL"));
        if($findPhone){
            $response = new Response(409, "Conflict");
            $return['response'] = $response;
            $return['code'] = 2;
            return $return;
        }
    }

    if(isset($_POST['name'])){
        $name = $filter->sanitize($_POST['name'], "string");
        $user->setName($name);
    }

    $password = $_POST['password'];
    $date = new DateTime(); $salt = $date->getTimestamp();
    $password = hash('sha256', $password.$salt);
    $user->setPassword($password);
    $user->setSalt($salt);


    try{
        if ($user->save()) {
            $response = new Response(201, "Created");
            $user = Users::findFirst($user->getId());
            $user->setTokenMobile(hash('sha256', "mobile".$user->getId().$user->getEmail().$date->getTimestamp()));

            if($user->save()){
                $user = AppUser::toArrayMobile($user);
                $return['data'] = $user;            
            }

        } else {
            $response = new Response(500, "Internal server error");
            $errors = array();
            foreach ($user->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
            $return['data'] = $errors;
        }
    }catch(InvalidArgumentException $e){
        $return['exception'] = $e->getMessage();
        $response = new Response(400, "Bad Request");
    }catch(PDOException $e){
        $response = new Response(409, "Conflict");
    }    

    $return['response'] = $response;
    return $return;
});

//****************
//*** PUT
//****************

//Login to actualize Token
$app->put('/login/{id:[0-9]+}', function($id) use ($app) 
{
    $filter = new Filter();

    if($app->userId!=$id){
        $response = new Response(403, "Forbidden");
        $return['response'] = $response;
        $return['data'] = '';
        return $return;
    }
    
    $id = $filter->sanitize($id, "int");
    $query = "id=$id AND status=1";

    $user = Users::findFirst(array($query));

    if($user){
        $date = new DateTime();
        $user->setTokenMobile(hash('sha256', "mobile".$user->getId().$user->getEmail().$date->getTimestamp()));
        if($user->save()){
            $res = AppUser::toArrayMobile($user);
            $response = new Response(200, "OK");
            $return['data'] = $res;
        }
        else{            
            $response = new Response(500, "Internal server error");
            // Send errors to the client
            $errors = array();
            foreach ($user->getMessages() as $message) {
                $errors[] = $message->getMessage();
            }
            $return['data'] = $errors;
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }
    $return['response'] = $response;
    return $return;
});

$app->put('/users/{id:[0-9]+}', function($id) use ($app) 
{
    $filter = new Filter();

    if(!$this->request->hasPut("email") && !$this->request->hasPut("phone")){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $userId = $app->userId;

    if($id!=$userId){
        $response = new Response(403, "Forbidden");
        $return['response'] = $response;
        return $return;
    }

    $email = $filter->sanitize($this->request->getPut('email'), "email");
    $prefix = $filter->sanitize($this->request->getPut('prefix'), "int");
    $phone = $filter->sanitize($this->request->getPut('phone'), "int");
    //$name = $filter->sanitize($this->request->getPut('name'), "int");

    $user = Users::findFirst($id);

    if($user){

        /*$userEmail = Users::findFirst(array("email='$email'"));
        if($userEmail && $userEmail->getId()!=$userId){
            $response = new Response(409, "Conflict");
            $return['response'] = $response;
            $return['data'] = 1;
            return $return;            
        }

        $query = "prefixId=$prefix AND phone LIKE '$phone'";
        echo $query;
        $userPhone = Users::findFirst(array("prefixId=$prefix AND phone LIKE '$phone'"));

        echo json_encode($userPhone);

        if($userPhone && $userPhone->getId()!=$userId){
            $response = new Response(409, "Conflict");
            $return['response'] = $response;
            $return['data'] = 0;
            return $return;            
        }*/

        $user->setEmail($email);
        $user->setPrefix($prefix);
        $user->setPhone($phone);
        if($this->request->hasPut("name")){
            $name = $filter->sanitize($this->request->getPut('name'), "string");
            $user->setName($name);
        }

        try{
            if($user->save()){
                $response = new Response(200 , "OK");
                //$return['data'] = $user->getPhone();
            }
            else{
                $response = new Response(500, "Internal server error");            
            }
        }catch(InvalidArgumentException $e){
            $return['exception'] = $e->getMessage();
            $response = new Response(400, "Bad Request");
        }catch(PDOException $e){
            $response = new Response(409, "Conflict");
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

$app->put('/users/{id:[0-9]+}/email', function($id) use ($app) 
{
    $filter = new Filter();

    if(!$this->request->hasPut("value")){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $userId = $app->userId;

    if($id!=$userId){
        $response = new Response(403, "Forbidden");
        $return['response'] = $response;
        return $return;
    }

    $email = $filter->sanitize($this->request->getPut('value'), "email");
    $user = Users::findFirst($id);

    if($user){
        try{
            $user->setEmail($email);
            if($user->save()){
                $response = new Response(200 , "OK");
                $return['data'] = $user->getEmail();
            }
            else{
                $response = new Response(500, "Internal server error");            
            }
        }
        catch(InvalidArgumentException $e){
            $return['exception'] = $e->getMessage();
            $response = new Response(400, "Bad Request");
        }
        catch(PDOException $e){
            $response = new Response(409, "Conflict");
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

$app->put('/users/{id:[0-9]+}/name', function($id) use ($app) 
{
    $filter = new Filter();

    if(!$this->request->hasPut("value")){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $userId = $app->userId;

    if($id!=$userId){
        $response = new Response(403, "Forbidden");
        $return['response'] = $response;
        return $return;
    }

    $name = $filter->sanitize($this->request->getPut('value'), "string");
    $user = Users::findFirst($id);

    if($user){
        try{
            $user->setName($name);
            if($user->save()){
                $response = new Response(200 , "OK");
                $return['data'] = $user->getName();
            }
            else{
                $response = new Response(500, "Internal server error");            
            }
        }
        catch(InvalidArgumentException $e){
            $return['exception'] = $e->getMessage();
            $response = new Response(400, "Bad Request");
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

$app->put('/users/{id:[0-9]+}/phone', function($id) use ($app) 
{
    $filter = new Filter();

    if(!$this->request->hasPut("value")){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $userId = $app->userId;

    if($id!=$userId){
        $response = new Response(403, "Forbidden");
        $return['response'] = $response;
        return $return;
    }

    $phone = $filter->sanitize($this->request->getPut('value'), "int");
    if($this->request->hasPut("prefix"))
        $prefixId = $filter->sanitize($this->request->getPut('prefix'), "int");
    else
        $prefixId = null;

    $user = Users::findFirst($id);

    if($user){
        try{
            $user->setPrefix($prefixId);
            $user->setPhone($phone);
            if($user->save()){
                $response = new Response(200 , "OK");
                $return['data'] = $user->getPhone();
            }
            else{
                $response = new Response(500, "Internal server error");            
            }
        }
        catch(InvalidArgumentException $e){
            $return['exception'] = $e->getMessage();
            $response = new Response(400, "Bad Request");
        }catch(PDOException $e){
            $response = new Response(409, "Conflict");
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

$app->put('/users/{id:[0-9]+}/password', function($id) use ($app) 
{
    $filter = new Filter();

    if(!$this->request->hasPut("password") && !$this->request->hasPut("currentPassword")){
        $response = new Response(400, "Bad Request");
        $return['response'] = $response;
        return $return;
    }

    $userId = $app->userId;

    if($id!=$userId){
        $response = new Response(403, "Forbidden");
        $return['response'] = $response;
        return $return;
    }

    $newPass = $this->request->getPut("password");
    $currentPass = $this->request->getPut("currentPassword");

    $user = Users::findFirst($id);

    if($user){

        $currentPass = hash('sha256', $currentPass.$user->getSalt());

        if($user->getPassword()!=$currentPass){
            $response = new Response(403, "Forbidden");
            $return['response'] = $response;
            return $return;
        }

        $password = hash('sha256', $newPass.$user->getSalt());
        $user->setPassword($password);

        if($user->save()){
            $response = new Response(200 , "OK");
            //$return['data'] = $user->getPhone();
        }
        else{
            $response = new Response(500, "Internal server error");            
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

//****************
//*** DELETE
//****************
$app->delete('/users/{id:[0-9]+}', function($id) use ($app) 
{
    $userId = $app->userId;

    if($id!=$userId){
        $response = new Response(403, "Forbidden");
        $return['response'] = $response;
        return $return;
    }

    $user = Users::findFirst(array("id=$id AND status!='deleted'"));

    if($user){
        if($user->delete()){
            $response = new Response(204 , "No Content");
        }
        else{
            $response = new Response(500, "Internal server error");
        }
    }
    else{
        $response = new Response(404, "Not Found");
    }

    $return['response'] = $response;
    return $return;
});

?>