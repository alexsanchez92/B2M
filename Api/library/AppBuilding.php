<?php

class AppBuilding
{
    public static function toArray($building)
    {
        $res = array();
        $res['id'] = $building->getId();
        $res['name'] = $building->getName();

        return $res;
    }
}