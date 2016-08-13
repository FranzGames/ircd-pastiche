package org.pastiche.ircd.rfc1459;

/*
 *   Pastiche IRCd - Java Internet Relay Chat
 *   Copyright (C) 2001 Charles Miller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pastiche.ircd.IrcdConfiguration;
import org.pastiche.ircd.Target;
import org.pastiche.ircd.Server;
import org.pastiche.ircd.Mask;

/**
 * <p>
 * This class officially annoys me. I don't know how to do user capabilities
 * thing in a sane fashion. Any patterns would be appreciated. Right now, the
 * Command is responsible for determining whether the user can do something to
 * the Channel, and the Channel just becomes a rather dumb data-holding object.
 */
public class Channel extends org.pastiche.ircd.Channel {

   private String topic = null;
   private java.util.Set ops = new java.util.HashSet();
   private java.util.Set voiced = new java.util.HashSet();
   private java.util.Set invited = new java.util.HashSet();
   private java.util.Set bans = new java.util.HashSet();
   private boolean topicRestricted = false;
   private boolean restrictExternalMessages = false;
   private boolean moderated = false;
   private boolean inviteOnly = false;
   private boolean memberLimited = false;
   private String key = null;
   private int memberLimit = 0;
   private ChannelVisibility visibility = ChannelVisibility.VISIBLE;

   public Channel() {
      super();
   }

   public void add(org.pastiche.ircd.Target user) throws org.pastiche.ircd.AlreadyOnChannelException {
      super.add(user);

      if (getMembers().length == 1) {
         ops.add(user);
      }

      java.util.Iterator i = getVisibleLocalTargets().iterator();

      while (i.hasNext()) {
         ((org.pastiche.ircd.Target) i.next()).send(user, "JOIN :" + getName());
      }
   }

   public void addBan(Target source, String mask) {
      bans.add(new Ban(new NickUserHostMask(mask), source.getName()));
   }

   public void addInvitation(Target source) {
      invited.add(source);
   }

   public boolean canSend(org.pastiche.ircd.Target source) {
      if (!isOnChannel(source)) {
         return !(isRestrictExternalMessages() || isModerated());
      }

      if (isModerated()) {
         return isOp(source) || isVoiced(source);
      }

      return true;
   }

   public void deop(org.pastiche.ircd.Target t) {
      ops.remove(t);
   }

   public void deVoice(Target target) {
      voiced.remove(target);
   }

   public static org.pastiche.ircd.Channel findOrCreateChannel(Server server, String name) {
      org.pastiche.ircd.Channel channel;

      if ((channel = server.getChannel(name)) != null) {
         return channel;
      }

      try {
         channel = (Channel) IrcdConfiguration.getInstance().getChannelClass().newInstance();
         
         channel.init(server, name);
      } catch (org.pastiche.ircd.BadChannelNameException bcne) {
         return findOrCreateChannel(server,
            ((ChannelNormalizer) org.pastiche.ircd.IrcdConfiguration.getInstance().getChannelNormalizer()).fixChannelName(name));
      } catch (InstantiationException ex) {
         ex.printStackTrace();
      } catch (IllegalAccessException ex) {
         ex.printStackTrace();
      }
      
      return channel;
   }

   public java.util.Set getBans() {
      return java.util.Collections.unmodifiableSet(bans);
   }

   public String getKey() {
      return key;
   }

   public int getMaximumMessageSize() {
      return UnregisteredClient.MAX_MESSAGE_SIZE;
   }

   public int getMaximumMessageSize(org.pastiche.ircd.Target source) {
      return UnregisteredClient.MAX_MESSAGE_SIZE - source.getLongName().length() - 2;
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 10:40:23
    * AM)
    *
    * @return int
    */
   public int getMemberLimit() {
      return memberLimit;
   }

   public String getNickListModifier(Target target) {
      if (isOp(target)) {
         return "@";
      }

      if (isVoiced(target)) {
         return "+";
      }

      return "";
   }

   public String getTopic() {
      return topic;
   }

   public boolean hasBan(String nickUserHost) {
      java.util.Iterator i = bans.iterator();
      Mask mask = new NickUserHostMask(nickUserHost);

      while (i.hasNext()) {
         if (((Ban) i.next()).getMask().equals(mask)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBanned(String nickUserHost) {
      java.util.Iterator i = bans.iterator();
      Mask mask = new NickUserHostMask(nickUserHost);

      while (i.hasNext()) {
         if (((Ban) i.next()).matches(mask)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBanned(RegisteredUser user) {
      java.util.Iterator i = bans.iterator();
      while (i.hasNext()) {
         if (((Ban) i.next()).matches(user)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInvited(Target source) {
      return invited.contains(source);
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 12:26:17
    * AM)
    *
    * @return boolean
    */
   public boolean isInviteOnly() {
      return inviteOnly;
   }

   public boolean isLocked() {
      return key != null;
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 10:40:23
    * AM)
    *
    * @return boolean
    */
   public boolean isMemberLimited() {
      return memberLimited;
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 12:06:15
    * AM)
    *
    * @return boolean
    */
   public boolean isModerated() {
      return moderated;
   }

   public boolean isOp(Target target) {
      return ops.contains(target);
   }

   public boolean isPrivate() {
      return visibility.equals(ChannelVisibility.PRIVATE);
   }

   /**
    * Insert the method's description here. Creation date: (24/02/2001 10:46:16
    * PM)
    *
    * @return boolean
    */
   public boolean isRestrictExternalMessages() {
      return restrictExternalMessages;
   }

   public boolean isSecret() {
      return visibility.equals(ChannelVisibility.SECRET);
   }

   public boolean isTopicRestricted() {
      return topicRestricted;
   }

   public boolean isVisible() {
      return visibility.equals(ChannelVisibility.VISIBLE);
   }

   public boolean isVoiced(Target target) {
      return voiced.contains(target);
   }

   public void lock(String key) {
      this.key = key;
   }

   private void notifyMembersOfNewTopic(org.pastiche.ircd.Target setter) {
      org.pastiche.ircd.Target[] members = getMembers();

      for (int i = 0; i < members.length; i++) {
         members[i].send(setter, "TOPIC " + getName() + " :" + getTopic());
      }
   }

   public void op(org.pastiche.ircd.Target t) {
      ops.add(t);
   }

   public void remove(org.pastiche.ircd.Target user, String notifyCommandLine) throws org.pastiche.ircd.NotOnChannelException {
      super.remove(user);
      removeInvitation(user);
      deop(user);
      deVoice(user);

      java.util.Iterator i = getVisibleLocalTargets().iterator();

      while (i.hasNext()) {
         ((org.pastiche.ircd.Target) i.next()).send(user, notifyCommandLine);
      }

      user.send(user, notifyCommandLine);
   }

   public void removeBan(String mask) {
      bans.remove(new Ban(new NickUserHostMask(mask), null));
   }

   public void removeInvitation(Target source) {
      invited.remove(source);
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 12:26:17
    * AM)
    *
    * @param newInviteOnly boolean
    */
   public void setInviteOnly(boolean newInviteOnly) {
      inviteOnly = newInviteOnly;
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 10:40:23
    * AM)
    *
    * @param newMemberLimit int
    */
   public void setMemberLimit(int newMemberLimit) {
      memberLimit = newMemberLimit;
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 10:40:23
    * AM)
    *
    * @param newMemberLimited boolean
    */
   public void setMemberLimited(boolean newMemberLimited) {
      memberLimited = newMemberLimited;
   }

   /**
    * Insert the method's description here. Creation date: (25/02/2001 12:06:15
    * AM)
    *
    * @param newModerated boolean
    */
   public void setModerated(boolean newModerated) {
      moderated = newModerated;
   }

   /**
    * Insert the method's description here. Creation date: (24/02/2001 10:46:16
    * PM)
    *
    * @param newRestrictExternalMessages boolean
    */
   public void setRestrictExternalMessages(boolean newRestrictExternalMessages) {
      restrictExternalMessages = newRestrictExternalMessages;
   }

   public void setTopic(org.pastiche.ircd.Target setter, String topic) {
      this.topic = topic;
      notifyMembersOfNewTopic(setter);
   }

   public void setTopicRestricted(boolean topicRestricted) {
      this.topicRestricted = topicRestricted;
   }

   public void setVisibility(ChannelVisibility visibility) {
      this.visibility = visibility;
   }

   public void unlock() {
      this.key = null;
   }

   public void voice(Target target) {
      voiced.add(target);
   }
}
