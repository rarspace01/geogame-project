class User < ActiveRecord::Base

include Clearance::User

  has_many :flags

  has_many :items, as: :itemowner

  def getPoints

  points = Flag.sum(:prestige, :conditions => ['user_id = ?',self.id])

  return points

  end


end
