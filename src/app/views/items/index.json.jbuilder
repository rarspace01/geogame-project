json.array!(@items) do |item|
  json.extract! item, :name, :itemtype, :price
  json.url item_url(item, format: :json)
end
