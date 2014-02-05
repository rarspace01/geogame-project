json.extract! @vendor, :name, :location_lat, :location_lng, :created_at, :updated_at

json.items @vendor.items do |json, item|
  json.(item , :id, :name, :itemtype, :price)
end
