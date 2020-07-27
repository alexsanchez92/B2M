<?php
    use Phalcon\Filter as Filter;

//obtenemos todos los Perdidos
$app->get('/countries', function() use ($app) 
{   
    $countries = Countries::find(array('columns'=> 'id, name, prefix, iso2, iso3', 'order'=>'name asc'));
    $count = Countries::count();

    $response = new Response(200, "OK");

    $return['response'] = $response;
    $return['count'] = $count;
    $return['data'] = $countries;
    /*$return['data'] = array();

    foreach($losts as $lost){
        $res = AppLost::toArray($lost);
        array_push($return['data'], $res);
    }*/
    return $return;
});

?>