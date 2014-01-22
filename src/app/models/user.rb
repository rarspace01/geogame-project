class User < ActiveRecord::Base

include Clearance::User

  #has_many :flags, dependent: destroy

end
