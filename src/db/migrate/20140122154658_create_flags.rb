class CreateFlags < ActiveRecord::Migration
  def change
    create_table :flags, :id => false do |t| # disables PK creation
      t.integer :id, :limit => 8 # That makes the column type bigint
      t.integer :user_id
      t.integer :prestige

      t.timestamps
    end
  end
end
