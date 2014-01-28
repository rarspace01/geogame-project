class CreateVendors < ActiveRecord::Migration
  def change
    create_table :vendors do |t|
      t.string :name
      t.float :location_lat
      t.float :location_lng

      t.timestamps
    end
  end
end
