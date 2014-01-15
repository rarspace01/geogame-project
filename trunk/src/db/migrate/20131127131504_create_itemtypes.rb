class CreateItemtypes < ActiveRecord::Migration
  def change
    create_table :itemtypes do |t|
      t.string :name
      t.integer :xp
      t.integer :app

      t.timestamps
    end
  end
end
