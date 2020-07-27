<?php

class AppFound
{
    public static function toArray($found)
    {
        $res = array();

        $res['id'] = $found->getId();
        $res['title'] = $found->getTitle();
        $res['description'] = $found->getDescription();
        $res['image'] = 'http://'.$_SERVER['HTTP_HOST'].ABS_URL_IMAGE_FOUNDS.$found->getImage();
        $res['hasPhoto'] = $found->getHasPhoto();
        $res['foundDate'] = $found->getFoundDate();
        $res['property'] = $found->getProperty();
        $res['haveIt'] = $found->getHaveIt();
        $res['createdAt'] = $found->getCreatedAt();
        $res['status'] = $found->getStatus();
        $res['type'] = 0;

        $res['user'] = AppUser::toArrayMin($found->users);
        
        if($found->buildings){
            $res['place'] = AppBuilding::toArray($found->buildings);
            $res['place']['placeDetails'] = $found->getPlaceDetails();
        }

        if(!$found->getHaveIt() && $found->holder){
            $res['holder'] = AppBuilding::toArray($found->holder);
            $res['holder']['havePlaceDetails'] = $found->getHavePlaceDetails();
        }

        return $res;
    }

    public static function toArrayWithUser($found, $user)
    {
        $res = array();

        $res['id'] = $found->getId();
        $res['title'] = $found->getTitle();
        $res['description'] = $found->getDescription();
        $res['image'] = 'http://'.$_SERVER['HTTP_HOST'].ABS_URL_IMAGE_FOUNDS.$found->getImage();
        $res['hasPhoto'] = $found->getHasPhoto();
        $res['foundDate'] = $found->getFoundDate();
        $res['property'] = $found->getProperty();
        $res['haveIt'] = $found->getHaveIt();
        $res['createdAt'] = $found->getCreatedAt();
        $res['status'] = $found->getStatus();
        $res['type'] = 0;

        $res['user'] = AppUser::toArrayMin($user);
        
        if($found->buildings){
            $res['place'] = AppBuilding::toArray($found->buildings);
            $res['place']['placeDetails'] = $found->getPlaceDetails();
        }

        if(!$found->getHaveIt() && $found->Holder){
            $res['holder'] = AppBuilding::toArray($found->Holder);
            $res['holder']['havePlaceDetails'] = $found->getHavePlaceDetails();
        }

        return $res;
    }
}