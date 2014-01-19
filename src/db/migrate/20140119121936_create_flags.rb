class CreateFlags < ActiveRecord::Migration
  def change
    create_table :flags do |t|
      t.integer :owner
      t.integer :prestige

      t.timestamps
    end
  end
end
