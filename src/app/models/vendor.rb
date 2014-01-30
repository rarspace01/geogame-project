class Vendor < ActiveRecord::Base

  has_many :items, as: :itemowner

end
