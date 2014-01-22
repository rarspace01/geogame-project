class CreateFlags < ActiveRecord::Migration
  def change
    create_table :flags do |t|
      t.belongs_to :users
      t.integer :prestige

      t.timestamps
    end
  end
end
