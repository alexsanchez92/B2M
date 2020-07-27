<?php
 
use Phalcon\Mvc\Model,
    Phalcon\Mvc\Model\Message,
    Phalcon\Mvc\Model\Validator\InclusionIn,
    Phalcon\Mvc\Model\Validator\Uniqueness,
    Phalcon\Mvc\Model\Behavior\SoftDelete;
 
class Countries extends Model
{
    protected $id;
    protected $iso2;
    protected $iso3;
    protected $prefix;
    protected $name;

    public function initialize()
    {
        $this->setSource("countries");
        //$this->skipAttributes( array( 'name', 'fbUser', 'gUser', 'twUser', 'phone' ) );

        $this->belongsTo("id", "Users", "prefixId");

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

    public function getPrefix()
    {
        return '+'.$this->prefix;
    }

    public function getIso2()
    {
        return $this->iso2;
    }

    public function getIso3()
    {
        return $this->iso3;
    }
}