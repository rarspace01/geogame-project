class GameboardController < ApplicationController
  include GameboardHelper
  def show

    @geojson = getJSONfromOverpass(params[:overpass])

  end
end
