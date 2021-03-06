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
  updateintervall = 60*60 # seconds

  # check if update is needed
  
    timeNow = Time.now.to_i
    timeLast = self.aplastupdate

    # calc current ap
    deltaAp = ((timeNow-timeLast)/updateintervall).to_i
    self.aplastupdate = timeLast+(deltaAp)*updateintervall
    self.ap = [(deltaAp+self.ap),24].min if(self.ap<=24)

    self.save

    return self.ap

  end


end
