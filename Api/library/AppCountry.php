<?php

class AppCountry
{
    public static function toArray($country)
    {
        $res = array();
        $res['id'] = $country->getId();
        $res['name'] = $country->getName();
        $res['prefix'] = $country->getPrefix();

        return $res;
    }
}