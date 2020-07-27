<?php
 
use Phalcon\Mvc\Model,
    Phalcon\Mvc\Model\Message,
    Phalcon\Mvc\Model\Validator\InclusionIn,
    Phalcon\Mvc\Model\Validator\Uniqueness,
    Phalcon\Mvc\Model\Behavior\SoftDelete;
 
class Users extends Model
{
    protected $id;
    protected $email;
    protected $prefixId;
    protected $phone;
    protected $name;
    protected $password;
    protected $salt;
    protected $token;
    protected $tokenMobile;
    protected $status;
    protected $createdAt;
    protected $updatedAt;

    public function initialize()
    {
        $this->setSource("users");
        $this->skipAttributes( array( 'status', 'createdAt', 'updatedAt' ) );

        $this->addBehavior(
            new SoftDelete( array( 'field' => 'status', 'value' => 3 ) )
        );

        /*$this->validate(new Uniqueness(
            array(
                "field"   => "email",
                "message" => "Email must be unique"
            )
        ));

        $this->validate(new Uniqueness(
            array(
                "field"   => "phone",
                "message" => "Phone must be unique"
            )
        ));*/

        $this->hasMany("id", "Lost", "userId");
        $this->hasMany("id", "Found", "userId");

        $this->belongsTo("prefixId", "Countries", "id");

        //$this->hasManyToMany('uniqueId', 'Friends', 'userId', 'friendId', 'Users', 'uniqueId', array('alias'=>'friendsFollowing'));
        //$this->hasManyToMany('uniqueId', 'Friends', 'friendId', 'userId', 'Users', 'uniqueId', array('alias'=>'friendsFollowers'));
    }

    public function getId()
    {
        return $this->id;
    }

    public function getEmail()
    {
        return $this->email;
    }
    public function setEmail($email)
    {
        if (strlen($email) > 100) {
            throw new \InvalidArgumentException('Email is too long');
        }
        $this->email = $email;
    }

    public function getPhone()
    {
        return $this->phone;
    }
    public function setPhone($phone)
    {
        if (strlen($phone) > 25) {
            throw new \InvalidArgumentException('Phone is too long');
        }
        $this->phone = $phone;
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

    public function getPassword()
    {
        return $this->password;
    }
    public function setPassword($password)
    {
        $this->password = $password;
    }

    public function getSalt()
    {
        return $this->salt;
    }
    public function setSalt($salt)
    {
        $this->salt = $salt;
    }

    public function getToken()
    {
        return $this->token;
    }
    public function setToken($token)
    {
        $this->token = $token;
    }

    public function getTokenMobile()
    {
        return $this->tokenMobile;
    }
    public function setTokenMobile($tokenMobile)
    {
        $this->tokenMobile = $tokenMobile;
    }

    public function getStatus()
    {
        return $this->status;
    }
    public function setStatus($status)
    {
        if($status>3 && $status<1) {
            throw new \InvalidArgumentException('Status not valid');
        }
        $this->status = $status;
    }
    
    public function getCreatedAt()
    {
        return $this->createdAt;
    }

    public function getPrefix()
    {
        return $this->prefixId;
    }
    public function setPrefix($prefixId)
    {
        $this->prefixId = $prefixId;
    }
}