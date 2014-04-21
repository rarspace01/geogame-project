class VendorsController < ApplicationController
  before_action :set_vendor, only: [:show, :edit, :update, :destroy, :addItem, :buyItem]

  # GET /vendors
  # GET /vendors.json
  def index
    @vendors = Vendor.all
  end

  # GET /vendors/getVendors.json
  def getVendors

  # bounding box
  n = params[:n]
  s = params[:s]
  w = params[:w]
  e = params[:e]

  # get vendors in bounding box
  @vendors = Vendor.where(location_lat: (s..n)).where(location_lng: (w..e))

  # build GeoJSON
  featureList = Hash.new

  featureList["type"] = "FeatureCollection"
  featureList["features"] = Array.new 

  # iterate over the vendors from db
  @vendors.each do |vendor|
	  feature = Hash.new
	  feature["type"] = "Feature"
	  geometry = Hash.new
	  geometry["type"] = "Point"
	  geometry["coordinates"] = [vendor.location_lng,vendor.location_lat]
	  properties = Hash.new
	  properties["popupContent"] = vendor.name
	  feature["geometry"] = geometry
	  feature["properties"] = properties
	  feature["id"] = vendor.id
	  featureList["features"].push(feature)
  end
  @vendors_geojson = JSON.parse(featureList.to_json)

  end

  # GET /vendors/addItem/vendorid/itemid
  def addItem
  @itemToBeAdded = Item.find_by_id(params[:itemid])
  @vendor.items.push(@itemToBeAdded)

  respond_to do |format|
  format.html { redirect_to vendors_url }
  format.json { head :no_content }

  end

  end

  # GET /vendors/buyItem/vendorid/itemid
  def buyItem
  @itemToBeBuyed = Item.find_by_id(params[:itemid])

  # check if use is in range - disbaled. see thesis for details

  # check if use has enough money
  if(current_user.ap >= @itemToBeBuyed.price)
  # push to user & remove from vendor
  current_user.items.push(@itemToBeBuyed)
  @vendor.items.delete(@itemToBeBuyed)

  current_user.ap = current_user.ap - @itemToBeBuyed.price

  current_user.save

  end

  respond_to do |format|
  format.html { redirect_to root_path }
  format.json { head :no_content }

  end

  end

  # GET /vendors/1
  # GET /vendors/1.json
  def show
    @unassignedItems = Item.find_all_by_itemowner_id(nil)
  end

  # GET /vendors/new
  def new
    @vendor = Vendor.new
  end

  # GET /vendors/1/edit
  def edit
  end

  # POST /vendors
  # POST /vendors.json
  def create
    @vendor = Vendor.new(vendor_params)

    respond_to do |format|
      if @vendor.save
        format.html { redirect_to @vendor, notice: 'Vendor was successfully created.' }
        format.json { render action: 'show', status: :created, location: @vendor }
      else
        format.html { render action: 'new' }
        format.json { render json: @vendor.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /vendors/1
  # PATCH/PUT /vendors/1.json
  def update
    respond_to do |format|
      if @vendor.update(vendor_params)
        format.html { redirect_to @vendor, notice: 'Vendor was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: 'edit' }
        format.json { render json: @vendor.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /vendors/1
  # DELETE /vendors/1.json
  def destroy
    @vendor.destroy
    respond_to do |format|
      format.html { redirect_to vendors_url }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_vendor
      if(params[:id] != "getVendors")
      @vendor = Vendor.find(params[:id])
      end
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def vendor_params
      params.require(:vendor).permit(:name, :location_lat, :location_lng, :n, :s, :w, :e)
    end
end
