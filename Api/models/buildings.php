<?php
 
use Phalcon\Mvc\Model,
    Phalcon\Mvc\Model\Message,
    Phalcon\Mvc\Model\Validator\InclusionIn,
    Phalcon\Mvc\Model\Validator\Uniqueness,
    Phalcon\Mvc\Model\Behavior\SoftDelete;
 
class Buildings extends Model
{
    protected $id;
    protected $name;
    protected $lat;
    protected $lng;

    public function initialize()
    {
        $this->setSource("buildings");
        //$this->skipAttributes( array( 'name', 'fbUser', 'gUser', 'twUser', 'phone' ) );

        $this->belongsTo("id", "Lost", "placeId");
        $this->belongsTo("id", "Found", "placeId");

        //$this->hasManyToMany('uniqueId', 'Friends', 'userId', 'friendId', 'Users', 'uniqueId', array('alias'=>'friendsFollowing'));
        //$this->hasManyToMany('uniqueId', 'Friends', 'friendId', 'userId', 'Users', 'uniqueId', array('alias'=>'friendsFollowers'));
    }

    public function getId()
    {
        return $this->id;
    }

    public function getName()
    {
        return $this->name;
    }
    public function setName($name)
    {
        if (strlen($name) > 100) {
            throw new \InvalidArgumentException('Name is too long');
        }
        $this->name = $name;
    }

    public function getLat()
    {
        return $this->lat;
    }

    public function setLat($lat)
    {
        $this->lat = $lat;
    }

    public function getLng()
    {
        return $this->lng;
    }

    public function setLng($lng)
    {
        $this->lng = $lng;
    }
}