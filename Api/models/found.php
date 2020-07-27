<?php
 
use Phalcon\Mvc\Model,
    Phalcon\Mvc\Model\Message,
    Phalcon\Mvc\Model\Validator\InclusionIn,
    Phalcon\Mvc\Model\Validator\Uniqueness,
    Phalcon\Mvc\Model\Behavior\SoftDelete;
 
class Found extends Model
{
    protected $id;
    protected $userId;
    protected $title;
    protected $description;
    protected $image;
    protected $hasPhoto;
    protected $foundDate;
    protected $placeId;
    protected $placeDetails;
    protected $property;
    protected $haveIt;
    protected $havePlaceId;
    protected $havePlaceDetails;
    protected $status;
    protected $createdAt;
    protected $updatedAt;

    public function initialize()
    {
        $this->setSource("found");
        //$this->skipAttributes( array( 'description', 'haveIt', 'placeId', 'placeDetails', 'hasPhoto') );

        $this->addBehavior(
            new SoftDelete( array( 'field' => 'status', 'value' => 4 ) )
        );

        $this->belongsTo("userId", "Users", "id");
        $this->belongsTo("placeId", "Buildings", "id");

        $this->belongsTo("havePlaceId", "Buildings", "id", array('alias'=> 'holder'));
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

    public function getFoundDate()
    {
        return $this->foundDate;
    }
    public function setFoundDate($foundDate)
    {
        $this->foundDate = $foundDate;
    }

    public function getProperty()
    {
        return $this->property;
    }
    public function setProperty($property)
    {
        if (strlen($property) > 50) {
            throw new \InvalidArgumentException('Property is too long');
        }
        $this->property = $property;
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

    public function getHaveIt()
    {
        return $this->haveIt;
    }
    public function setHaveIt($haveIt)
    {
        $this->haveIt = $haveIt;
    }

    public function getHavePlaceId()
    {
        return $this->havePlaceId;
    }
    public function setHavePlaceId($havePlaceId)
    {
        $this->havePlaceId = $havePlaceId;
    }

    public function getHavePlaceDetails()
    {
        return $this->havePlaceDetails;
    }
    public function setHavePlaceDetails($havePlaceDetails)
    {
        if (strlen($havePlaceDetails) > 300) {
            throw new \InvalidArgumentException('Details is too long');
        }
        $this->havePlaceDetails = $havePlaceDetails;
    }
}