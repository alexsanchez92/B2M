<?php
    use Phalcon\Filter as Filter;

//obtenemos todos los Perdidos
$app->get('/buildings', function() use ($app) 
{   
    $buildings = Buildings::find(array('columns'=> 'id, name', 'order'=>'name asc'));
    $count = Buildings::count();

    $response = new Response(200, "OK");

    $return['response'] = $response;
    $return['count'] = $count;
    $return['data'] = $buildings;
    /*$return['data'] = array();

    foreach($losts as $lost){
        $res = AppLost::toArray($lost);
        array_push($return['data'], $res);
    }*/
    return $return;
});

?>