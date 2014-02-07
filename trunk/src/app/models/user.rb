class User < ActiveRecord::Base

include Clearance::User

  has_many :flags

  has_many :items, as: :itemowner

  def getPoints

  points = Flag.sum(:prestige, :conditions => ['user_id = ?',self.id])

  return points

  end

  def getAp

  # default ap
  if(self.ap == nil)
    self.ap = 24
  end

  # default last update
  if(self.aplastupdate == nil)
    self.aplastupdate = 0
  end

  #updateintervall
  updateintervall = 60 # minutes

  # check if update is needed
  if(Time.now.to_i > (self.aplastupdate + 60*updateintervall))

    #do update when ap under 24
    if(self.ap<24)

    # calc update diff
     diff = ((Time.now.to_i - (self.aplastupdate + 60*updateintervall) )/60).round

     if(self.ap + diff>24)
       self.ap = 24
     else
       self.ap = self.ap + diff
     end

    end

    #
    self.aplastupdate = Time.now.to_i

    self.save

  

  end

    return self.ap

  end


end
