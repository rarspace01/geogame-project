class User < ActiveRecord::Base

include Clearance::User

  has_many :flags

  def getPoints

  points = Flag.sum(:prestige, :conditions => ['user_id = ?',self.id])

  return points

  end


end
