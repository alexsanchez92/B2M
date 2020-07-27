<?php

class AppLost
{
    public static function toArray($lost)
    {
        $res = array();

        $res['id'] = $lost->getId();
        $res['title'] = $lost->getTitle();
        $res['description'] = $lost->getDescription();
        $res['image'] = 'http://'.$_SERVER['HTTP_HOST'].ABS_URL_IMAGE_LOSTS.$lost->getImage();
        $res['startDate'] = $lost->getStartDate();
        $res['endDate'] = $lost->getEndDate();
        //$res['placeId'] = $lost->getPlaceId();
        $res['hasPhoto'] = $lost->getHasPhoto();
        $res['createdAt'] = $lost->getCreatedAt();
        $res['status'] = $lost->getStatus();
        $res['type'] = 1;

        $res['user'] = AppUser::toArrayMin($lost->users);

        if($lost->buildings){
            $res['place'] = AppBuilding::toArray($lost->buildings);
            $res['place']['placeDetails'] = $lost->getPlaceDetails();
        }

        return $res;
    }

    public static function toArrayWithUser($lost, $user)
    {
        $res = array();

        $res['id'] = $lost->getId();
        $res['title'] = $lost->getTitle();
        $res['description'] = $lost->getDescription();
        $res['image'] = 'http://'.$_SERVER['HTTP_HOST'].ABS_URL_IMAGE_LOSTS.$lost->getImage();
        $res['startDate'] = $lost->getStartDate();
        $res['endDate'] = $lost->getEndDate();
        //$res['placeId'] = $lost->getPlaceId();
        $res['hasPhoto'] = $lost->getHasPhoto();
        $res['status'] = $lost->getStatus();
        $res['createdAt'] = $lost->getCreatedAt();
        $res['status'] = $lost->getStatus();
        $res['type'] = 1;

        $res['user'] = AppUser::toArrayMin($user);
        
        if($lost->buildings){
            $res['place'] = AppBuilding::toArray($lost->buildings);
            $res['place']['placeDetails'] = $lost->getPlaceDetails();
        }

        return $res;
    }
}