class Item < ActiveRecord::Base

belongs_to :itemowner, polymorphic: true

end
