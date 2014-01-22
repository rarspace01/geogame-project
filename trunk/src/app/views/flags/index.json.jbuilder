json.array!(@flags) do |flag|
  json.extract! flag, :owner, :prestige
  json.url flag_url(flag, format: :json)
end
