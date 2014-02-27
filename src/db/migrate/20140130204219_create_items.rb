class CreateItems < ActiveRecord::Migration
  def change
    create_table :items do |t|
      t.string :name

      t.references :itemowner, polymorphic: true

      t.integer :itemtype
      t.integer :price

      t.timestamps
    end
  end
end