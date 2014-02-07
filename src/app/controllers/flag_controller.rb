class FlagController < ApplicationController
  #include GameboardHelper
  require 'rgeo'
  include OverpassApiHelper
  def show

  #Flag
  cid = params[:id]
  # default Flag
  currentFlag = Flag.new
  currentFlag.id = cid
  currentFlag.prestige = 0

  #get owner
  dbflag = Flag.find_by_id(cid)
  if(dbflag != nil)
  currentFlag = dbflag;
  currentFlag.user_id
  @owner = User.find_by_id(currentFlag.user_id)
        if(@owner != nil)
  @ownerName = @owner.email
  else 
  @ownerName = "Neutral"  
  end
  else
  @ownerName = "Neutral"  
  end  

  @prestige = currentFlag.prestige

  currentgeojson = get_geojson_byid(cid)

  @id = params[:id]
  @flaglat = currentgeojson["geometry"]["coordinates"][1]
  @flaglng = currentgeojson["geometry"]["coordinates"][0]
  @userlat = params[:lat]
  @userlng = params[:lng]

  # Geographic factory that projects to a world mercator projection.
  # Note the ellps and datum set to WGS84.
  factory = ::RGeo::Geographic.simple_mercator_factory()

  userlocation = factory.point(@userlng, @userlat)
  flaglocation = factory.point(@flaglng, @flaglat)

  @distance = userlocation.distance(flaglocation)

  #interaction possible?
  if(@distance <40)
    @isInteractable = true;
  else
    @isInteractable = false;    
  end
  #is owner?
  if(current_user == @owner)
    @isowner = true;
  else
    @isowner = false;
  end

  end

  def attack

  #check for interaction distance

  #is user logged in?
  if(signed_in? && params[:id] != nil)
  
    # get current flag from db
    #Flag
    cid = params[:id]
    @owner = User.new
    # default Flag
    @currentFlag = Flag.new
    @currentFlag.id = cid
    @currentFlag.prestige = 0

    dbflag = Flag.find_by_id(cid)
    if(dbflag != nil)
    @currentFlag = dbflag
    @owner = User.find_by_id(@currentFlag.user_id)
    end

  #has actionpoints left
  if(current_user.ap>0)

      #is owner?
    if(current_user == @owner)
      @isowner = true
      @currentFlag.prestige = @currentFlag.prestige+1 
      
    else
      @isowner = false
      # prestige >0
      if(@currentFlag.prestige>0)
        @currentFlag.prestige = @currentFlag.prestige-1
        #if new prestige == 0 then delete owner
        if(@currentFlag.prestige == 0)
        @currentFlag.user_id = nil
        end
      # prestige == 0
      else
        @currentFlag.prestige = 1
        @currentFlag.user_id = current_user.id
      end
    end
  # lower ap from user
        current_user.ap = current_user.ap-1
        current_user.save
  end

  #save currentFlag to db
        @currentFlag.save
  end

  end

end
