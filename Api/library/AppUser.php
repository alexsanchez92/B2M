<?php

class AppUser
{
    public static function toArray($user)
    {
        $res = array();
        $res['id'] = $user->getId();
        $res['email'] = $user->getEmail();
        //$res['prefix'] = $user->getPrefix();
        $res['phone'] = $user->getPhone();
        $res['name'] = $user->getName();
        $res['token'] = $user->getToken();

        if($user->countries){
            $res['prefix'] = AppCountry::toArray($user->countries);
        }

        return $res;
    }

    public static function toArrayMobile($user)
    {
        $res = array();
        $res['id'] = $user->getId();
        $res['email'] = $user->getEmail();
        //$res['prefix'] = $user->getPrefix();
        $res['phone'] = $user->getPhone();
        $res['name'] = $user->getName();
        $res['token'] = $user->getTokenMobile();

        if($user->countries){
            $res['prefix'] = AppCountry::toArray($user->countries);
        }

        return $res;
    }

    public static function toArrayMin($user)
    {
        $res = array();
        $res['id'] = $user->getId();
        $res['email'] = $user->getEmail();
        //$res['prefix'] = $user->getPrefix();
        $res['phone'] = $user->getPhone();
        $res['name'] = $user->getName();

        if($user->countries){
            $res['prefix'] = AppCountry::toArray($user->countries);
        }

        return $res;
    }
}