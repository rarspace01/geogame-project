class CreatePlayers < ActiveRecord::Migration
  def change
    create_table :players do |t|
      t.string :nickname
      t.string :email
      t.integer :level
      t.integer :xp
      t.integer :ap
      t.integer :app

      t.timestamps
    end
  end
end
