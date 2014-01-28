json.array!(@vendors) do |vendor|
  json.extract! vendor, :name, :location_lat, :location_lng
  json.url vendor_url(vendor, format: :json)
end
