class CreateFlags < ActiveRecord::Migration
  def change
    create_table :flags do |t|
      t.integer :value
      t.integer :player_id
      t.timestamp :lastdecay

      t.timestamps
    end
  end
end
