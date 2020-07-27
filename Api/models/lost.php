<?php
 
use Phalcon\Mvc\Model,
    Phalcon\Mvc\Model\Message,
    Phalcon\Mvc\Model\Validator\InclusionIn,
    Phalcon\Mvc\Model\Validator\Uniqueness,
    Phalcon\Mvc\Model\Behavior\SoftDelete;
 
class Lost extends Model
{
    protected $id;
    protected $userId;
    protected $title;
    protected $description;
    protected $image;
    protected $hasPhoto;
    protected $startDate;
    protected $endDate;
    protected $placeId;
    protected $placeDetails;
    protected $status;
    protected $createdAt;
    protected $updatedAt;

    public function initialize()
    {
        $this->setSource("lost");
        //$this->skipAttributes( array( 'description', 'endDate', 'placeDetails', 'hasPhoto') );

        $this->addBehavior(
            new SoftDelete( array( 'field' => 'status', 'value' => 4 ) )
        );

        $this->belongsTo("userId", "Users", "id");
        $this->belongsTo("placeId", "Buildings", "id");
    }

    //**********
    //***  Getters and setters
    //**********
    public function getId()
    {
        return $this->id;
    }

    public function getUserId()
    {
        return $this->userId;
    }
    public function setUserId($userId)
    {
        $this->userId = $userId;
    }

    public function getTitle()
    {
        return $this->title;
    }
    public function setTitle($title)
    {
        if (strlen($title) > 50) {
            throw new \InvalidArgumentException('Title is too long');
        }
        $this->title = $title;
    }

    public function getDescription()
    {
        return $this->description;
    }
    public function setDescription($description)
    {
        if (strlen($description) > 300) {
            throw new \InvalidArgumentException('Description is too long');
        }
        $this->description = $description;
    }

    public function getImage()
    {
        return $this->image;
    }
    public function setImage($image)
    {
        $this->image = $image;
    }

    public function getHasPhoto()
    {
        return $this->hasPhoto;
    }
    public function setHasPhoto($hasPhoto)
    {
        $this->hasPhoto = $hasPhoto;
    }

    public function getStartDate()
    {
        return $this->startDate;
    }
    public function setStartDate($startDate)
    {
        $this->startDate = $startDate;
    }

    public function getEndDate()
    {
        return $this->endDate;
    }
    public function setEndDate($endDate)
    {
        $this->endDate = $endDate;
    }

    public function getPlaceId()
    {
        return $this->placeId;
    }
    public function setPlaceId($placeId)
    {
        $this->placeId = $placeId;
    }

    public function getPlaceDetails()
    {
        return $this->placeDetails;
    }
    public function setPlaceDetails($placeDetails)
    {
        if (strlen($placeDetails) > 300) {
            throw new \InvalidArgumentException('Details is too long');
        }
        $this->placeDetails = $placeDetails;
    }

    public function getStatus()
    {
        return $this->status;
    }
    public function setStatus($status)
    {
        if($status>4 && $status<1) {
                throw new \InvalidArgumentException('Status not valid');
            }
        $this->status = $status;
    }

    public function getCreatedAt()
    {
        return $this->createdAt;
    }
}